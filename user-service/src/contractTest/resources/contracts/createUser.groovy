package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should create a new user"
    name "createUser"

    request {
        method 'POST'
        url "/api/users"
        headers {
            header("x-api-key", "INTERNAL_API_KEY")
            contentType(applicationJson())
        }
        body([
                username: "new_user",
                firstName: "New",
                lastName: "User",
                email: "new.user@example.com",
                password: "securePassword",
                phoneNumber: "+380509999999",
                userRole: "USER"
        ])
    }

    response {
        status CREATED()
        headers {
            contentType(applicationJson())
            header("Location", "/api/users/2")
        }
        body([
                id: 2,
                username: "new_user",
                firstName: "New",
                lastName: "User",
                email: "new.user@example.com",
                userRole: "USER",
                phoneNumber: "+380509999999",
                dateOfBirth: null
        ])
    }
}