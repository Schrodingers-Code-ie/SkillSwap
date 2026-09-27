import { Outlet } from "react-router";

export async function protectedLoader() {
    // React Router loader() that runs before the protected routes are rendered.
    // Authentication check will be added later when Spring Boot authentication is ready.

    return null;
}

export default function ProtectedRouter() {
    return <Outlet />;
}