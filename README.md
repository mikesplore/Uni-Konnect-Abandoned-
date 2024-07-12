# Uni-Konnect
## Documentation

### GitHub Authentication ([GitAuth](app/src/main/java/com/mike/unikonnect/GithubAuth.kt))

The GitAuth composable allows users to authenticate with GitHub using [Firebase Authentication](https://firebase.google.com/docs/auth). Here's how it works:

#### Initialization:

The composable receives three parameters: _firebaseAuth_ (an instance of FirebaseAuth), _onSignInSuccess_ (a callback function for successful sign-in), and _onSignInFailure_ (a callback function for sign-in failure).
The activity is obtained from the local context using LocalContext.current.
An OAuthProvider for GitHub is created using **OAuthProvider.newBuilder("github.com")**.

#### State Management:

A mutable state variable _isLoading_ is used to track whether the sign-in process is in progress.

#### UI and Click Handling:

A Box composable is used to create the button UI.
When the button is clicked, isLoading is set to true to indicate the start of the sign-in process.
The firebaseAuth.startActivityForSignInWithProvider method is called to initiate the sign-in process.
On success, isLoading is set to false, and onSignInSuccess is called.
On failure, isLoading is set to false, onSignInFailure is called with the error message, and the error is logged.
Loading Indicator and Button Content:

If isLoading is true, a CircularProgressIndicator is displayed.
If isLoading is false, an image representing GitHub is displayed.


### Google Authentication (GoogleAuth)

The GoogleAuth composable allows users to authenticate with Google using Firebase Authentication. Here's how it works:

#### Initialization:

The composable receives three parameters: _firebaseAuth_ (an instance of FirebaseAuth), _onSignInSuccess_ (a callback function for successful sign-in), and _onSignInFailure_ (a callback function for sign-in failure).
The activity is obtained from the local context using LocalContext.current.
An OAuthProvider for Google is created using OAuthProvider.newBuilder("google.com").
Scopes are added to the provider for profile, email, and OpenID Connect authentication.
State Management:

A mutable state variable _isLoading_ is used to track whether the sign-in process is in progress.

### UI and Click Handling:

A Box composable is used to create the button UI.
When the button is clicked, isLoading is set to true to indicate the start of the sign-in process.
The firebaseAuth.startActivityForSignInWithProvider method is called to initiate the sign-in process.
On success, isLoading is set to false, and onSignInSuccess is called.
On failure, isLoading is set to false, onSignInFailure is called with the error message, and the error is logged.
Loading Indicator and Button Content:

If isLoading is true, a CircularProgressIndicator is displayed.
If isLoading is false, an image representing Google is displayed.

## Styling and Appearance

Both **GitAuth** and **GoogleAuth** components use similar styling:

The buttons are created using a Box composable with a clickable modifier to handle click events.
The buttons have a border and background defined by [CommonComponents (CC)](app/src/main/java/com/mike/unikonnect/CommonComponents.kt) utility functions.
The buttons have a fixed height and width.
Content alignment is set to center to ensure that the loading indicator or the provider image is centered within the button.

#### Error Handling

Both components handle errors during the sign-in process:

isLoading is set to false to stop the loading indicator.
The _onSignInFailure_ callback is invoked with the error message.
Errors are logged using Log.e.
