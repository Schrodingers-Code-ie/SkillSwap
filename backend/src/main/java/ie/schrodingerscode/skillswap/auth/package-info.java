/**
 * Sign up, log in and "who is logged in" for SkillSwap.
 *
 * <p>The package is split into layers, and each one only depends on the layers above it in this list:
 * <ul>
 *   <li>{@code domain}: the {@code User} and {@code Email} rules and the {@code UserRepository} interface.
 *       No Spring or database code here.</li>
 *   <li>{@code application}: the use cases ({@code RegisterUser}, {@code LoginUser}) that the controllers call.</li>
 *   <li>{@code infrastructure}: the JPA code that saves users to the {@code users} table.</li>
 *   <li>{@code web}: the REST controllers and the JSON request and response records.</li>
 * </ul>
 *
 * <p>Other features should only use {@link ie.schrodingerscode.skillswap.auth.CurrentUser} to get the
 * logged-in user's ID, and {@link ie.schrodingerscode.skillswap.auth.application.UserChecker} to check
 * that a user exists. They should not use the repository or the table directly.
 */
package ie.schrodingerscode.skillswap.auth;
