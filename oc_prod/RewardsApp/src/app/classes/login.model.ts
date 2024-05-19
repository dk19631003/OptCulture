

    export interface Header {
        requestId: string;
        requestDate: string;
        docSID: string;
        sourceType: string;
    }

    export interface Membership {
        password: string;
        phoneNumber: string;
        emailId: string;
        OTP: string;
        instanceId: string;
        deviceId: string;
    }

    export interface User {
        userName: string;
        organizationId: string;
        token: string;
    }

    export interface LoginRequest {
        header: Header;
        membership: Membership;
        user: User;
    }



