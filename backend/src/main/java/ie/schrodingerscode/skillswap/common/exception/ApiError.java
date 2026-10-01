package ie.schrodingerscode.skillswap.common.exception;

/**java gives:
 * a constructor (new ApiError(404, "User not found"))
 * getter and setter methods (error.status() and error.message())
 * equals(), hashCode(), and toString()
**/
public record ApiError(int status, String message) {

}
