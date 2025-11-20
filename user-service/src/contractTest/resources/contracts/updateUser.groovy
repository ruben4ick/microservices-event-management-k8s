package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should update existing user"
    name "updateUser"

    request {
        method 'PUT'
        url "/api/users/1"
        headers {
            header("x-api-key", "INTERNAL_API_KEY")
            contentType(applicationJson())
        }
        body([
                userRole: "USER",
                username: "test_user_updated",
                firstName: "Test",
                lastName: "User",
                email: "testemail@email.com",
                password: "password",
                phoneNumber: "+38068888888",
                dateOfBirth: "2000-01-01"
        ])
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                id: 1,
                userRole: "USER",
                username: "test_user_updated",
                firstName: "Test",
                lastName: "User",
                email: "testemail@email.com",
                password: "password",
                phoneNumber: "+38068888888",
                dateOfBirth: "2000-01-01"
        ])
    }
}