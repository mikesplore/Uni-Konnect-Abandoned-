# Uni-Konnect
## Documentation

##Login
This screen allows users to sign in or sign up using either [Google](#google-authentication-googleauth) or [GitHub](#github-authentication-gitauth) authentication or by entering their email and password. The UI includes animated visibility transitions, dynamic text fields, and authentication handling with Firebase. The composable also includes options for password reset and toggling between sign-in and sign-up modes.

Key Components
State Variables:

firstName, lastName, email, password: Stores user input.
isSigningUp: Boolean to toggle between sign-in and sign-up modes.
isGithubLoading, isGoogleLoading, loading: Booleans to indicate loading states during authentication.
visible: Controls visibility animation for the main content.
LaunchedEffect:

Sets visible to true to trigger the entrance animation when the composable is first launched.
AnimatedVisibility:

Controls the slide-in and slide-out animations of the main content.
Scaffold:

Provides the structure for the screen, including a top app bar and a content area.
The TopAppBar shows the title ("Sign Up" or "Sign In") and a back button icon.
The containerColor is set using a custom color from CC.primary().
Column Layout:

The main layout is a vertically arranged Column with spacing and alignment properties.
GoogleAuth and GitAuth:

Custom composables for Google and GitHub authentication buttons.
On successful sign-in, it fetches the Firebase Cloud Messaging (FCM) token and writes it to the database.
On failure, it shows a toast message.
Text Fields:

CC.SingleLinedTextField and CC.PasswordTextField are custom composables for text input.
These fields are conditionally displayed based on isSigningUp.
Button:

A button to either sign up or sign in based on the current mode.
On click, it handles authentication using FirebaseAuth.
During loading, it shows a CircularProgressIndicator.
Password Reset and Mode Toggle:

A clickable text to navigate to the password reset screen.
A row with text to toggle between sign-in and sign-up modes.
Detailed Flow
Initialization:

Firebase Authentication instance is initialized.
Mutable state variables are defined to manage user inputs and states.
UI Setup:

The Scaffold component sets up the top bar and the background using a gradient brush.
The main content is displayed within an AnimatedVisibility component to animate its appearance.
Top Bar:

Displays "Sign Up" or "Sign In" based on isSigningUp.
Includes a back button (navigation logic not implemented).
Authentication Options:

Google and GitHub authentication buttons are displayed.
On successful sign-in, user details are fetched, and the FCM token is retrieved and stored in the database.
On failure, a toast message is displayed.
Email and Password Fields:

Conditional rendering of text fields based on the authentication mode.
Sign-up mode includes additional fields for the first and last names.
Sign In/Sign Up Button:

Handles both sign-in and sign-up logic.
On sign-up, it creates a new user in FirebaseAuth and stores user details in the database.
On sign-in, it authenticates the user and navigates to the appropriate screen based on the user's data.
Additional Options:

Includes clickable text for password reset and toggling between sign-in and sign-up modes.


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


### Google Authentication ([GoogleAuth](app/src/main/java/com/mike/unikonnect/GoogleAuth.kt))

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
