# CheddarSafe-Android

## What is it?
Cheddar Safe is an Android app that functions as an easy gateway to all your account information.
To access your accounts, you would start by logging in with a master username and password that was set upon account creation.
Once logged in, you can look around in your folders to find your account information without having to have it all memorized or written down somewhere else.

Some features of the app include:
- Add/remove accounts as needed.
- Organize user accounts into folders for ease-of-use.
- Sort the folders and accounts in the folders for easier access.
- Change master username, password, and email for user account.

## How does it work?
The general flow of use for the app is as follows:
1. The user opens the app for the first time and requests to create an account.
2. After providing a valid username and password, the user will be prompted for a valid email.
3. An email is sent to the email address which contains a link that once clicked, will allow the user to login with their credentials.
4. Once logged in, the user can then create folders and put accounts into them as they please.

## How is it secure?
Cheddar Safe provides a safe and secure gateway to storing and accessing your accounts. 
It accomplishes this goal in multiple ways which include:
- Pulling/Pushing data from the Cheddar Safe API over SSL to encrypt user data
- When setup, requires a fingerprint on top of a master username/password to login
- Requires a valid email address in the situation that the user's master password is forgotten
- Has reCAPTCHA integrated into the account creation/recovery processes to protect against bots

## Known Bugs
Here is a list of some currently known bugs and (if possible) how to fix them:
- Sometimes when the user logs in, all folders and accounts are show twice even though they are only in the database once. To fix, close the app, relaunch, and log in.
- When editing an existing account, if the user wants to change just the username and password without changing the ID, then the user must change the ID along with the username and password changes, save the changes, then change the ID back to the previous one.
- When changing view mode from vertical to horizontal, some of the elements get lost off the screen. It is highly recommended to only use the app in vertical mode to provide the best user experience.

## Planned Features and Fixes
- Adding a search bar to quickly search and find an account by its ID.
- Update the UI with more appealing icons.
- Assuring no duplicates of entries when logging in.
- Allowing the user to just change the username and password of accounts if desired.
- More customization settings.
- Allow user the delete their account.
- Provide button to quickly move an account from one folder to another.
- Add setting to remove fingerprint authentication.
- Implement clipboard feature to allow for quick copy/pasting of credentials.

## FAQ
### 1. Is this app publicly available to be downloaded?
As of right now, this app is not available on the Play Store. There is currently a plan to publish this on the Play Store eventually,
but many revisions are to be done before that happens.

### 2. If it is not available to be downloaded, how do I visually see how it looks/works?
On the How To wiki page, there are videos along with step-by-step guides on how to do different things within the app, which will be updated as new features and fixes are added.

### 3. How can I submit suggestions or feedback on the code provided?
If you read the code and encounter potential issues or want to provide helpful criticism, please email __contact@compsg.dev__ with your comments and a response
will be sent back within the next 1-3 business days.
