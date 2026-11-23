/**
 * Blocking and unblocking other users.
 *
 * <p>The package is split into layers, and each one only depends on the layers above it in this list:
 * <ul>
 *   <li>{@code domain}: the {@code Block} rules and the {@code BlockRepository} interface.
 *       No Spring or database code here.</li>
 *   <li>{@code application}: {@code BlockService} for block and unblock, and {@code BlockLookup} that
 *       answers {@code BlockChecker} for other features.</li>
 *   <li>{@code infrastructure}: the JPA code that saves blocks to the {@code blocks} table.</li>
 *   <li>{@code web}: the REST controller for {@code /api/blocks}.</li>
 * </ul>
 *
 * <p>Blocking someone also removes any pending request or connection between the two users.
 * Other features should only use {@link ie.schrodingerscode.skillswap.block.application.BlockChecker}
 * to check if two users blocked each other. They should not use the repository or the table directly.
 */
package ie.schrodingerscode.skillswap.block;
