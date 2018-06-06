# Brew
An app for sharing tastes and recipes. 

#### Alpha Opt-in
Click this link to opt in. https://play.google.com/apps/testing/io.github.koss.brew

### Initial Thoughts
The initial goal of the app is to allow for users to note down things that they taste quickly and easily, and then add functionality to allow the user to share these tastes with their friends.

Brew is always going to be free and Open Source software. At later stages of development Brew will employ the usage of cloud-based platforms for storage of data, including images. For image hosting, platforms like [Imgur](https://imgur.com/) offer unlimited usage for free if not commercialised. Brew will _never_ be commercialised. For usage costs of services such as [Firebase](https://firebase.google.com/) thoughts are still to be had, however initially an NPO approach seems appropriate, where any excess funds are donated publically to yet-to-be-decided charities.

### Tentative Roadmap
Brew will be released in open beta as early as possible. The tentative contents of the beta versions are:

#### V0.1 (Beta)
Initial release of Brew containing the absolute basics:
- Local management of drinks you've recently tried
- Firebase Crash reporting and analytics

#### V0.2 (Beta)
Who needs local storage? V0.2 brings a basic introduction to the cloud, including:
- Image hosting 
- Hosting of your drinks
- Profiles with Firebase Auth, leading to..
- Users IDs! (To make doc storage more intelligent)

#### V0.3 (Beta)
We have profiles, user IDs and cloud data. What's next? Friends!
- Connect to your friends and see what they're drinking
- A first iteration of a home feed
- Friend interaction basics (i.e liking of drinks)

#### V0.4 (Beta)
Are we Instagram yet?
- Google maps integration with geo-fencing, leading to...
- Location tagging so you can see _where_ your friends enjoyed that tasty beverage

#### V0.5 (Beta) - V1.0
Further steps are yet to be decided. Some other things that'd be nice and might make it into V1.0 are:
- Drink recipes, so your friends can see how to make your drinks
- Proper backend for scalability using ktor
