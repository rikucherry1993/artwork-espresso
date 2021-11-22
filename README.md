--- Draft ---

## Installation
### 1. Get client ID & Secret 
Client ID & Secret of [DeviantArt](https://www.deviantart.com/) APIs are required for building this project.  
You can either create a new pair following the steps bellow:
- Sign up to [DeviantArt](https://www.deviantart.com/)  
- Go to [Developers](https://www.deviantart.com/developers/apps) and register an new application
  - In `Application Settings`, choose `Authorization Code` as OAuth2 Grant Type.
  - In `OAuth2 Redirect URI Whitelist`, add ``.  
- You will able to see your own client Id & Secret at [Developers](https://www.deviantart.com/developers/apps)

Or, use the following credentials for testing.
- API secrets  
```
Client ID : XXXXX
Client Secret: XXXXXXXXXXXX
```  
- User credentials. 
```
User Name:
Password: 
```

### 2. Hide API secrets in a C/C++ native file
Using [hidden-secrets-gradle-plugin](https://github.com/klaxit/hidden-secrets-gradle-plugin).  
Execute gradlew commands at the top level of this package as below:  
```
./gradlew hideSecret -Pkey={yourClientIdToObfuscate} -PkeyName=ClientId -Ppackage=com.rikucherry.artworkespresso
./gradlew hideSecret -Pkey={yourClientSecretToObfuscate} -PkeyName=ClientSecret -Ppackage=com.rikucherry.artworkespresso
```
