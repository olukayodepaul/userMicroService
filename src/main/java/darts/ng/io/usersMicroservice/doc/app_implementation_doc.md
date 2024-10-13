#### User Registration

You can use the following `curl` command to test the User Registration API:
- The Customer Registration API enables new users to register for an account on the platform. 
- This API accepts user details such as email, password, role, and additional personal information, allowing for the creation of a user profile within the system.
```bash
curl -X POST "api/account/register" \
-H "Content-Type: application/json" \
-d '{
  "email": "mucpkkj66njds@yaoo.co",
  "password": "@paul123#0O",
  "role": "Customer",
  "organisation_id": "1",
  "details": {
    "first_name": "first_name",
    "last_name": "last_name",
    "phone_number": "9098778876567",
    "date_of_birth": "02-02-1983",
    "gender": "male",
    "bio": "this is paul"
  }
}'
```

#### Send Registration email confirmation to user
- The Send Confirmation Email API allows companies to send confirmation details to users via email. 
This API can be integrated into registration processes or other workflows requiring user verification, 
ensuring a seamless communication experience.
-  This endpoint is responsible for sending a confirmation email to users after successful registration. The email contains a link and code required to confirm the user's account.

```bash
curl -X POST "api/account/send-confirmation-email" \
-H "Content-Type: application/json" \
-d '{
  "email": "mucpkkj66njds@yaoo.co"
}'
```

#### Confirm Email API
- The Confirm Email API allows users to verify their email addresses by providing a confirmation code. This step is essential 
in ensuring the authenticity of the user's email, enhancing security, and improving the overall user experience.
-  This endpoint is used to verify and confirm the user's email address after registration. The user completes their registration by clicking the confirmation link sent via email or the verification code.
```bash
curl -X POST "api/account/confirm-email" \
-H "Content-Type: application/json" \
-d '{
  "email":"mmucdsp@yaoo.co",
  "confirmation_code_link":828685
}'
```
#### Blacklist User API
The Blacklist User API is used to block a user's access to the application temporarily. When a user is blacklisted, they cannot log in or use the app until the blacklist is lifted. This API allows administrators to specify the reason for blacklisting and set a duration (in seconds) for how long the restriction will last. It helps maintain a safe and compliant user environment.

```bash
curl -X POST "api/account/blacklist" \
-H "Authorization: Bearer <your_token>" \
-H "Content-Type: application/json" \
-d '{
  "reason": "Violation of terms",
  "period_in_second": "86400"
}'
```

#### Fetch Blacklist Details API
- Retrieves a paginated list of blacklisted user trail. 
- Blacklisted users cannot log in until their status is lifted.
```bash
curl -X GET "api/account/blacklist?offset=0&limit=10" \
-H "Authorization: Bearer <your_token>"
```

#### Remove User from Blacklist API
Removes a user from the blacklist. This action allows the user to regain access to the application.
```bash
curl -X DELETE "api/account/blacklist" \
-H "Authorization: Bearer YOUR_BEARER_TOKEN" \
-d '{
  "reason": "User requested removal"
}'
```

#### User Login API
Authenticates a user by validating their email and password. On successful login, the API returns a token for future authenticated requests.
```bash
curl -X POST "/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "password": "yourPassword123"
}'
```

#### Reset Password Information API
This API is designed to handle the password reset information, including the user's email, the reset code, and the new password. It must be integrated with the company's main service to ensure seamless communication and security. The API should facilitate the retrieval and processing of reset requests and should be capable of interfacing with the email service for sending reset codes to users.
```bash
curl -X POST "/api/auth/request-password-reset" \
-H "Content-Type: application/json" \
-d '{
    "email": "user@example.com"
}'
```

#### Reset Password Information API
This API allows users to reset their password without having to log in. The user must provide their email address, the reset code sent to them, and the new password they wish to set. This API should be integrated with the company's main service to ensure security and proper handling of the password reset process.
```bash
curl -X POST "/api/auth/reset-password" \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "reset_code": "123456",
  "new_password": "newSecurePassword123!"
}'
```

#### API Endpoint: Change Password for Logged-In Users
This API allows a logged-in user to change their password securely. It requires the user to provide their current password along with the new password and its confirmation. The Authorization header ensures that the request is authenticated.
```bash
curl -X PUT "http://localhost:8090/api/account/reset-password" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer your_token_here" \
-d '{
    "old_password": "current_password_here",
    "new_password": "new_password_here",
    "confirm_password": "confirm_new_password_here"
}'
```