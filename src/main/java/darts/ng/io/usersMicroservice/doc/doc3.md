**Basic Registration (AccountService/AuthService)**
json
Copy code
{
    "id": 1,
    "email": "user@example.com",
    "password": "hashed_password",
    "role": "user",
    "organisation_id": 101,
    "is_active": true,
    "created_at": "2024-09-26T10:00:00Z",
    "updated_at": "2024-09-26T10:00:00Z"
}


****Extended User Information (ProfileMicroService)
Once logged in, the user can update their profile settings in the ProfileMicroService. Here is an example of a JSON payload for that service:****

json
Copy code
{
    "user_id": 1,
    "first_name": "John",
    "last_name": "Doe",
    "phone_number": "+1234567890",
    "shipping_address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zip_code": "10001",
    "country": "USA"
},
    "billing_address": {
    "street": "456 Market St",
    "city": "New York",
    "state": "NY",
    "zip_code": "10001",
    "country": "USA"
},
    "profile_picture_url": "https://example.com/profile-pic.jpg",
    "preferences": {
    "language": "en",
    "currency": "USD"
},
    "updated_at": "2024-09-26T12:00:00Z"
}