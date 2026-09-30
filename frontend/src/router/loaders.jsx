import { redirect } from "react-router";

export async function homeLoader() {
    // Check authentication with Spring Boot.
    // If logged in, redirect to /matches.
    // If not logged in, redirect to /login.
    
    // Loader temprorary redirects to /login
    return redirect("/login");
}

export async function protectedLoader() {
    // Check authentication with Spring Boot.
    // If the user is not logged in, redirect to /login.
    
    return null;
}


