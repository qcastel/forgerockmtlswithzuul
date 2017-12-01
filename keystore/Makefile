#Variables:
PASSWORD=changeit

FOLDER_CA=ca
FOLDER_EUREKA=eureka
FOLDER_ZUUL=zuul
FOLDER_HELLO=hello


HOSTNAME_EUREKA=eureka.forgerock.example.com
HOSTNAME_ZUUL=zuul.forgerock.example.com
HOSTNAME_HELLO=hello.forgerock.example.com

DNAME_CA='CN=forgerock example CA,OU=forgerock.example.com,O=ForgeRock,L=Bristol,ST=Avon,C=UK'
DNAME_EUREKA='CN=$(HOSTNAME_EUREKA),OU=forgerock.example.com,O=ForgeRock,L=Bristol,ST=Avon,C=UK'
DNAME_ZUUL='CN=$(HOSTNAME_ZUUL),OU=forgerock.example.com,O=ForgeRock,L=Bristol,ST=Avon,C=UK'
DNAME_HELLO='CN=$(HOSTNAME_HELLO),OU=forgerock.example.com,O=ForgeRock,L=Bristol,ST=Avon,C=UK'

EUREKA_KEYSTORE_FOLDER=../forgerock-eureka/src/main/resources/keystore/
ZUUL_KEYSTORE_FOLDER=../forgerock-zuul/src/main/resources/keystore/
HELLO_KEYSTORE_FOLDER=../forgerock-hello/src/main/resources/keystore/

KEYSTORE=keystore.jks
TRUSTSTORE=truststore.jks
CA=ca.crt
CA_P12=ca.p12
#Makefile commands:

all:	clean \
 init-folder \
 create-authority \
 create-keystores \
 create-truststore \
 create-keys \
 deploy


init-folder: init-folder-ca init-folder-eureka init-folder-zuul init-folder-hello

init-folder-ca:
	# Generate ca folder
	mkdir ${FOLDER_CA}

init-folder-eureka:
	# Generate eureka folder
	mkdir ${FOLDER_EUREKA}

init-folder-zuul:
	# Generate zuul folder
	mkdir ${FOLDER_ZUUL}

init-folder-hello:
	# Generate hello folder
	mkdir ${FOLDER_HELLO}

create-authority:
	# Generate a CA
	keytool -genkey -alias ca -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_CA) \
	    -keystore $(FOLDER_CA)/$(KEYSTORE) -storepass $(PASSWORD)
	# Export CA public key
	keytool -export -alias ca -file $(FOLDER_CA)/$(CA) -rfc \
	    -keystore $(FOLDER_CA)/$(KEYSTORE) -storepass $(PASSWORD)
	# Export CA private key
	keytool -importkeystore -srcalias ca \
		-srckeystore $(FOLDER_CA)/$(KEYSTORE) -srcstorepass $(PASSWORD) \
		-destkeystore $(FOLDER_CA)/$(CA_P12) -deststorepass $(PASSWORD) \
		-deststoretype PKCS12

#
# Create keystores
#

create-keystores: create-eureka-keystore create-zuul-keystore create-hello-keystore

create-eureka-keystore:
	# Import CA into eureka keystore
	keytool -importkeystore \
	-deststorepass $(PASSWORD) -destkeystore $(FOLDER_EUREKA)/$(KEYSTORE) \
	-srckeystore $(FOLDER_CA)/$(CA_P12) -srcstoretype PKCS12 -srcstorepass $(PASSWORD)

create-zuul-keystore:
	# Import CA into zuul keystore
	keytool -importkeystore \
	-deststorepass $(PASSWORD) -destkeystore $(FOLDER_ZUUL)/$(KEYSTORE) \
	-srckeystore $(FOLDER_CA)/$(CA_P12) -srcstoretype PKCS12 -srcstorepass $(PASSWORD)

create-hello-keystore:
	# Import CA into hello keystore
	keytool -importkeystore \
	-deststorepass $(PASSWORD) -destkeystore $(FOLDER_HELLO)/$(KEYSTORE) \
	-srckeystore $(FOLDER_CA)/$(CA_P12) -srcstoretype PKCS12 -srcstorepass $(PASSWORD)

#
# Create Truststores
#

create-truststore:
	# Import CA into truststore
	keytool -import -trustcacerts -noprompt -alias ca -file $(FOLDER_CA)/$(CA) \
	    -keystore $(FOLDER_CA)/$(TRUSTSTORE) -storepass $(PASSWORD)


define generate_key
	$(eval HOSTNAME_=$(1))
	$(eval DNAME_=$(2))
	$(eval SAN_=$(3))
	$(eval KEYSTORE_=$(4))
	$(eval PASSWORD_=$(5))
	$(eval FOLDER_=$(6))
	$(eval BROWSER_CERT_=b-$(HOSTNAME).p12)

	@echo "Generate $(HOSTNAME_) certificate"
	keytool -genkey -alias $(HOSTNAME_) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD_) \
	    -validity 3650 -dname $(DNAME_) \
	    -ext SAN=$(SAN_) \
	    -keystore $(KEYSTORE_) -storepass $(PASSWORD_)

	@echo "Generate $(HOSTNAM_E) certificate signing request"
	keytool -certreq -alias $(HOSTNAME_) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "$(FOLDER_)/$(HOSTNAME_).csr" \
	    -ext SAN=$(SAN_) \
	    -keystore $(KEYSTORE_) -storepass $(PASSWORD_)

	@echo "Generate $(HOSTNAME_) signed certificate with the CA"
	keytool -gencert -alias ca \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(FOLDER_)/$(HOSTNAME_).csr" -outfile "$(FOLDER_)/$(HOSTNAME_).crt" -rfc \
	    -ext SAN=$(SAN_) \
	    -keystore $(KEYSTORE_) -storepass $(PASSWORD_)

	@echo "Import $(HOSTNAME_) signed certificate into $(KEYSTORE_)"
	keytool -import -trustcacerts -noprompt -alias $(HOSTNAME_) \
	    -file "$(FOLDER_)/$(HOSTNAME_).crt" \
	    -ext SAN=$(SAN_) \
	    -keystore $(KEYSTORE_) -storepass $(PASSWORD_)

	@echo "Export $(HOSTNAME_) private certificate for importing into a browser"
	keytool -importkeystore -srcalias $(HOSTNAME_) \
	    -srckeystore $(KEYSTORE_) -srcstorepass $(PASSWORD_) \
	    -destkeystore "$(FOLDER_)/$(BROWSER_CERT_)" -deststorepass $(PASSWORD_) \
	    -deststoretype PKCS12

	@echo "Export $(HOSTNAME_) certificate into .key"
	echo $(PASSWORD_) > /tmp/pwd.txt
	openssl pkcs12 -in $(FOLDER_)/$(BROWSER_CERT_)  -nodes -nocerts -out $(FOLDER_)/k-$(HOSTNAME_).key -passin file:/tmp/pwd.txt


endef
#
#
# Create Keys
#
create-keys: create-eureka-key create-zuul-key create-hello-key

create-eureka-key:
	$(eval SAN=dns:$(HOSTNAME_EUREKA))

	$(call generate_key,$(HOSTNAME_EUREKA),$(DNAME_EUREKA),\
	$(SAN),\
	$(FOLDER_EUREKA)/$(KEYSTORE),$(PASSWORD),$(FOLDER_EUREKA))

create-zuul-key:
	$(eval SAN=dns:$(HOSTNAME_ZUUL),dns:$(HOSTNAME_HELLO))

	$(call generate_key,$(HOSTNAME_ZUUL),$(DNAME_ZUUL),\
	$(SAN),\
	$(FOLDER_ZUUL)/$(KEYSTORE),$(PASSWORD),$(FOLDER_ZUUL))

create-hello-key:
	$(eval SAN=dns:$(HOSTNAME_HELLO))

	$(call generate_key,$(HOSTNAME_HELLO),$(DNAME_HELLO),\
	$(SAN),\
	$(FOLDER_HELLO)/$(KEYSTORE),$(PASSWORD),$(FOLDER_HELLO))

#
# Deploy keystores
#

deploy: deploy-eureka deploy-zuul deploy-hello

deploy-eureka:
	# copy eureka keystore
	cp $(FOLDER_EUREKA)/$(KEYSTORE) ${EUREKA_KEYSTORE_FOLDER}
	# copy eureka truststore
	cp $(FOLDER_CA)/$(TRUSTSTORE) ${EUREKA_KEYSTORE_FOLDER}

deploy-zuul:
	# copy zuul keystore
	cp $(FOLDER_ZUUL)/$(KEYSTORE) ${ZUUL_KEYSTORE_FOLDER}
	# copy zuul truststore
	cp $(FOLDER_CA)/$(TRUSTSTORE) ${ZUUL_KEYSTORE_FOLDER}

deploy-hello:
	# copy hello keystore
	cp $(FOLDER_HELLO)/$(KEYSTORE) ${HELLO_KEYSTORE_FOLDER}
	# copy hello truststore
	cp $(FOLDER_CA)/$(TRUSTSTORE) ${HELLO_KEYSTORE_FOLDER}

#
# Clean
#
clean: clean-ca clean-eureka clean-zuul clean-hello

clean-ca:
	# Remove ca folder
	rm -rf $(FOLDER_CA)
clean-eureka:
	# Remove eureka folder
	rm -rf ${FOLDER_EUREKA}
clean-zuul:
	# Remove zuul folder
	rm -rf ${FOLDER_ZUUL}
clean-hello:
	# Remove hello folder
	rm -rf ${FOLDER_HELLO}
