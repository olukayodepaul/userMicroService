##List of todo
##comment
##done
##in-progress


- create another feature that expire token and send token expiration notification (kafka/grpc) to other service -> todo
- re-factor the code -> todo
- re-factor the bruteforce attack protect algorithm -> todo
- create a system to save all the token use by individual users. mean we cant delete all this token.



- Note, there is no need implement account block on other microService, all you need is expire all the token and send it to
- other service. anything the person want to create a fresh token, the system, will not allow the user
- 