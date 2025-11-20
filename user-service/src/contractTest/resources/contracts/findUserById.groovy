package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should find a user given id"
    name "findUserById"

    request {
        method 'GET'
        url '/api/users/1'
        headers {
            header('x-api-key', 'INTERNAL_API_KEY')
        }
    }

    response {
        status OK()
        body([
                id: 1,
                userRole: "USER",
                username: "test_user",
                firstName: "Test",
                lastName: "User",
                email: "testemail@email.com",
                password: "password",
                phoneNumber: "+380677777777",
                dateOfBirth: $(producer(regex("\\d{4}-\\d{2}-\\d{2}")), consumer("2000-01-01"))
        ])
        headers {
            contentType(applicationJson())
        }
    }
}