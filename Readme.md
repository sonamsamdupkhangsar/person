# This is a demo Person Spring WebFlux project
This is a demo project for Spring WebFlux project.

Thre are 4 endpoints:
1. `/persons` POST for storing a new Person entity
2. `/persons` PUT for updating the Person entity by id
3. `/persons/{id}` GET for retrieving a person by id
4. `/persons` Get person or people by pages



1. POST to create a person by curl: 
```
curl -X POST http://localhost:8080/persons -H "Content-Type: application/json" -d '{"firstName": "Tashi", "lastName": "Tsering"}'
``` 

2. PUT to update the person by curl: 
```
curl -X PUT http://localhost:8080/persons -H "Content-Type: application/json" -d '{"id": "5ac0c195-0f8a-480d-8bb2-789d6a01378d", "firstName": "Tenzing", "lastName": "Lhamo"}'
```
   

3. GET to retrieve by id:
```
curl http://localhost:8080/persons/9b1e29dc-cd4b-4c60-b436-9093189cd2b4
```
4. Get persons by page:
```
curl  "http://localhost:8080/persons?page=0&size=100"
```