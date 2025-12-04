# Deployment Guide - Heroku

## Prerequisites

1. **Heroku Account**: Sign up at https://www.heroku.com (free tier available)
2. **Heroku CLI**: Install from https://devcenter.heroku.com/articles/heroku-cli
3. **Git**: Already installed (you're using it)

## Deployment Steps

### 1. Install Heroku CLI

**Mac:**
```bash
brew tap heroku/brew && brew install heroku
```

**Windows:**
Download and run the installer from https://devcenter.heroku.com/articles/heroku-cli

**Linux:**
```bash
curl https://cli-assets.heroku.com/install.sh | sh
```

### 2. Login to Heroku

```bash
heroku login
```

This will open a browser window for authentication.

### 3. Create Heroku App

```bash
cd /Users/ibrahimbah/PersonalProjects/chessgame
heroku create chessgame-ibra
```

Replace `chessgame-ibra` with your preferred app name (must be unique).

### 4. Compile Your Java Files

Before deploying, compile all Java files:

```bash
javac -d . -cp . src/main/java/board/*.java \
              src/main/java/pieces/*.java \
              src/main/java/utils/*.java \
              src/main/java/game/*.java \
              src/main/java/gui/*.java \
              src/main/java/network/*.java \
              src/main/java/ChessGame.java
```

### 5. Commit All Files

```bash
git add .
git commit -m "Prepare for Heroku deployment"
```

### 6. Deploy to Heroku

```bash
git push heroku main
```

If your default branch is `master` instead of `main`:
```bash
git push heroku master
```

### 7. Check Deployment Status

```bash
heroku logs --tail
```

### 8. Get Your Server URL

```bash
heroku info
```

The server will be accessible at: `your-app-name.herokuapp.com`

## Important Notes

### Current Limitation

The current setup deploys the **ChessServer** which accepts socket connections. However:

- **GUI clients** (ChessGUI) won't work directly in a browser - they need the desktop Java application
- **Network clients** can connect using the Heroku app URL as the server IP
- The server runs on the PORT assigned by Heroku (automatically configured)

### How to Connect Clients

1. **Get your Heroku app URL**: `your-app-name.herokuapp.com`
2. **Run the desktop client** on your computer:
   ```bash
   java ChessGame
   ```
3. **Select "Network Play (Online)"**
4. **Enter server IP**: `your-app-name.herokuapp.com`
5. **Enter port**: `80` or `443` (Heroku uses standard web ports)

### Alternative: Create Web Interface

To make it fully accessible in browsers, you would need to:
1. Create a web-based frontend (HTML/JavaScript)
2. Convert the server to use HTTP/WebSocket instead of raw sockets
3. Deploy both frontend and backend

## Troubleshooting

### Port Already in Use
Heroku automatically assigns a PORT - the code handles this via `System.getenv("PORT")`.

### Build Fails
- Ensure all Java files compile successfully
- Check that `system.properties` specifies Java 17
- Verify `Procfile` is correct

### Connection Issues
- Heroku apps sleep after 30 minutes of inactivity (free tier)
- First connection may take a few seconds to wake up
- Check logs: `heroku logs --tail`

### View Logs
```bash
heroku logs --tail
```

### Restart App
```bash
heroku restart
```

### Scale (if needed)
```bash
heroku ps:scale web=1
```

## Other Hosting Options

### Railway
1. Go to https://railway.app
2. Connect your GitHub repository
3. Select Java runtime
4. Set start command: `java -cp . network.ChessServer $PORT`

### Render
1. Go to https://render.com
2. Create new Web Service
3. Connect GitHub repository
4. Set build command: `javac -d . src/main/java/**/*.java`
5. Set start command: `java -cp . network.ChessServer $PORT`

### AWS Elastic Beanstalk
More complex but more powerful. See AWS documentation.

## Next Steps

For full web accessibility, consider:
1. Creating a web-based frontend
2. Converting socket communication to WebSocket
3. Deploying frontend to Vercel/Netlify
4. Deploying backend to Heroku/Railway

