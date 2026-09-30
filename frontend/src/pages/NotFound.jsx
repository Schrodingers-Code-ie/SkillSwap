import { Link } from "react-router";

export default function NotFound() {
  return (
    <div>
        <h1>404</h1>
        <p>Page not found.</p>
        <Link to="/login">Go to Login</Link>
    </div>
  );
}