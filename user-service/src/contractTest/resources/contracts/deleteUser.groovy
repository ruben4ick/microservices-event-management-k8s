package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should delete user by id"
    name "deleteUser"

    request {
        method 'DELETE'
        url "/api/users/1"
        headers {
            header("x-api-key", "INTERNAL_API_KEY")
        }
    }

    response {
        status NO_CONTENT()
    }
}