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

Now we start looking at the beast.

So I did implement a hostname mapping:
- See the zuul.yml config and the SimpleFilter
It works, as far as I can tell

- Try to access https://hello.forgerock.example.com:8083/mtlsTest

problem I got:
1) 
```$xslt
Certificate for <172.16.100.53> doesn't match any of the subject alternative names: [hello.forgerock.example.com]
```

That's due to zuul checking the hostname, even if sslHostnameValidationEnabled=false. Don't know why it's ignoring 
this option

EDIT: After digging a bit, I found I was affected by #251. The only solution I had was to upgrade to 2.0.0-M6 to get 
this fix.
EDIT: After investigating, I found out the option sslHostnameValidationEnabled wasn't doing its job. #2503
EDIT: Ribbon is causing me issues, by re-enabling hostname validation anyway. See #2506 Found a workaround for now.


2) When I hit my hello service via zuul, so https://hello.forgerock.example.com:8083/hello/mtlsTest
I got:

```$xslt
{"message":"Hello anonymous! Add your certificate into your web browser or postman to authenticate","authorities":[]}
```

When I hit the hello app directly, I got:

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


Thanks a lot of any help! I'm quite stuck.
