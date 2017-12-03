# ForgeRock MTLS test

First of all, thanks for talking time to look at our issue :)

#How to setup the project

- Open the project with intellij, you will find the shared configuration in the project
- Add the following hostname in your /etc/hosts 

```$xslt
127.0.0.1		config.forgerock.example.com eureka.forgerock.example.com  zuul.forgerock.example.com  hello.forgerock.example.com 
```
- generate a new pair of keys if like or use the one I generated already. If you want to generate new one:
    - go to keystore folder `cd keystore`
    - run the makefile `make all`
- add the keystore/ca/ca.crt in your trusted CA 
- Run the configuration server, then eureka, and the hello world service
- Go to https://hello.forgerock.example.com:8082/mtlsTest with chrome. You should have an answer like:

```$xslt
{
    message: "Hello subject: CN=forgerock example CA, OU=forgerock.example.com, O=ForgeRock, L=Bristol, ST=Avon, C=UK!",
    authorities: [
        {
            authority: "AUTHENTICATED"
        }
    ]
}
```

- I found easier to add the ca to the JVM truststore
```$xslt
 sudo keytool -import -trustcacerts -noprompt -alias ca-forgerock-example-mtls -file ca.crt -keystore cacerts -storepass changeit
```
but I recon there are better ways to workaround that. You are welcome to share the right way to make zuul happy with 
the self-signed certificate! :)

## Make zuul in front of the hello world



- Run Zuul
- Access https://hello.forgerock.example.com:8083/hello/mtlsTest
=> You should have an answer like:

```$xslt
{
    message: "Hello subject: CN=forgerock example CA, OU=forgerock.example.com, O=ForgeRock, L=Bristol, ST=Avon, C=UK!",
    authorities: [
        {
            authority: "AUTHENTICATED"
        }
    ]
}
```
