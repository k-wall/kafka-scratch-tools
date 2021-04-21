= Create Topics Until We Go Bang

```
export TOPIC=my-topic
export OAUTH_TOKEN_ENDPOINT_URI=https://identity.api.stage.openshift.com/auth/realms/rhoas/protocol/openid-connect/token
export OAUTH_CLIENT_ID=srvc-acct-9a20b8b4-18e8-4e6c-a89e-12a118e4b86b
export OAUTH_CLIENT_SECRET=******************************************
export KAFKA_BOOTSTRAP_SERVERS=penguin-go--ruistftsqags-cxnklgj-kqp-v.kafka.devshift.org:443

mvn exec:java 
```

