# Free Hosting Options for Java Chess Server

Since Heroku no longer offers a free tier, here are the best **FREE** alternatives:

---

## 🚀 Option 1: Railway (Recommended - Easiest)

**Free Tier:** $5 credit/month (enough for small apps)

### Steps:

1. **Sign up:** Go to https://railway.app
2. **Connect GitHub:**
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Authorize Railway to access your GitHub
   - Select your `ChessGame` repository

3. **Configure:**
   - Railway auto-detects Java
   - Set **Start Command:** `java -cp . network.ChessServer $PORT`
   - Set **Root Directory:** `/` (or leave default)

4. **Deploy:**
   - Railway automatically builds and deploys
   - Get your URL: `your-app.railway.app`

**Pros:**
- ✅ Very easy setup
- ✅ Free $5 credit/month
- ✅ Auto-deploys from GitHub
- ✅ No credit card required for free tier

**Cons:**
- ⚠️ Requires credit card after $5 usage (but free tier is generous)

---

## 🎯 Option 2: Render (Completely Free)

**Free Tier:** 750 hours/month (enough for 24/7)

### Steps:

1. **Sign up:** Go to https://render.com
2. **Create Web Service:**
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select `ChessGame` repo

3. **Configure:**
   - **Name:** `chessgame` (or your choice)
   - **Environment:** `Java`
   - **Build Command:** 
     ```bash
     javac -d . -cp . src/main/java/board/*.java src/main/java/pieces/*.java src/main/java/utils/*.java src/main/java/game/*.java src/main/java/gui/*.java src/main/java/network/*.java src/main/java/ChessGame.java
     ```
   - **Start Command:** 
     ```bash
     java -cp . network.ChessServer $PORT
     ```
   - **Root Directory:** `/` (leave default)

4. **Deploy:**
   - Click "Create Web Service"
   - Render builds and deploys automatically
   - Get your URL: `your-app.onrender.com`

**Pros:**
- ✅ Completely free (no credit card needed)
- ✅ 750 hours/month (enough for 24/7)
- ✅ Auto-deploys from GitHub
- ✅ Free SSL certificate

**Cons:**
- ⚠️ Apps sleep after 15 minutes of inactivity (free tier)
- ⚠️ First request after sleep takes ~30 seconds

---

## 🔧 Option 3: Fly.io (Free Tier Available)

**Free Tier:** 3 shared VMs, 3GB storage

### Steps:

1. **Install Fly CLI:**
   ```bash
   curl -L https://fly.io/install.sh | sh
   ```

2. **Sign up:** Go to https://fly.io and create account

3. **Login:**
   ```bash
   fly auth login
   ```

4. **Create app:**
   ```bash
   cd /Users/ibrahimbah/PersonalProjects/chessgame
   fly launch
   ```

5. **Create `fly.toml`:**
   ```toml
   app = "chessgame-ibra"
   primary_region = "iad"

   [build]

   [http_service]
     internal_port = 8888
     force_https = true
     auto_stop_machines = true
     auto_start_machines = true
     min_machines_running = 0
     processes = ["app"]

   [[services]]
     protocol = "tcp"
     internal_port = 8888
   ```

6. **Deploy:**
   ```bash
   fly deploy
   ```

**Pros:**
- ✅ Generous free tier
- ✅ Global edge network
- ✅ Good performance

**Cons:**
- ⚠️ More complex setup
- ⚠️ Requires CLI installation

---

## 📦 Option 4: Oracle Cloud (Always Free)

**Free Tier:** Always free compute instances

### Steps:

1. **Sign up:** https://www.oracle.com/cloud/free/
2. **Create VM Instance:**
   - Choose "Always Free" tier
   - Select Ubuntu Linux
   - Configure security rules (open port 8888)

3. **SSH into VM:**
   ```bash
   ssh ubuntu@your-vm-ip
   ```

4. **Install Java:**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk -y
   ```

5. **Clone and deploy:**
   ```bash
   git clone https://github.com/Ibra2112/ChessGame.git
   cd ChessGame
   javac -d . -cp . src/main/java/**/*.java src/main/java/ChessGame.java
   java -cp . network.ChessServer 8888
   ```

**Pros:**
- ✅ Always free (no time limits)
- ✅ Full control
- ✅ No sleeping

**Cons:**
- ⚠️ More setup required
- ⚠️ Need to manage server yourself
- ⚠️ Requires credit card (but won't charge)

---

## 🎨 Option 5: Replit (Free Tier)

**Free Tier:** Always-on repls available

### Steps:

1. **Sign up:** https://replit.com
2. **Create Repl:**
   - Click "Create Repl"
   - Choose "Java" template
   - Name it "ChessGame"

3. **Upload files:**
   - Upload all your Java source files
   - Keep the same directory structure

4. **Configure:**
   - Create `.replit` file:
   ```toml
   run = "java -cp . network.ChessServer $PORT"
   ```

5. **Deploy:**
   - Click "Run"
   - Get your URL from Replit

**Pros:**
- ✅ Free tier available
- ✅ Easy to use
- ✅ Built-in editor

**Cons:**
- ⚠️ Limited resources on free tier
- ⚠️ May have connection limits

---

## 🏆 Recommendation: Use Render

**Why Render?**
- ✅ Completely free (no credit card)
- ✅ Easy GitHub integration
- ✅ Good documentation
- ✅ Free SSL
- ✅ 750 hours/month is plenty

**Quick Render Setup:**

1. Go to https://render.com
2. Sign up with GitHub
3. Click "New +" → "Web Service"
4. Connect your `ChessGame` repository
5. Set:
   - **Build Command:** `javac -d . -cp . src/main/java/board/*.java src/main/java/pieces/*.java src/main/java/utils/*.java src/main/java/game/*.java src/main/java/gui/*.java src/main/java/network/*.java src/main/java/ChessGame.java`
   - **Start Command:** `java -cp . network.ChessServer $PORT`
6. Click "Create Web Service"
7. Wait for deployment (2-5 minutes)
8. Get your URL!

---

## 📝 Quick Comparison

| Service | Free Tier | Credit Card | Ease | Best For |
|---------|-----------|-------------|------|----------|
| **Render** | 750 hrs/month | ❌ No | ⭐⭐⭐⭐⭐ | Most users |
| **Railway** | $5 credit/month | ✅ Yes | ⭐⭐⭐⭐⭐ | Easy setup |
| **Fly.io** | 3 VMs | ✅ Yes | ⭐⭐⭐ | Advanced users |
| **Oracle Cloud** | Always free | ✅ Yes | ⭐⭐ | Full control |
| **Replit** | Limited | ❌ No | ⭐⭐⭐⭐ | Quick testing |

---

## 🚀 Next Steps

1. **Choose Render** (easiest, completely free)
2. **Sign up** at https://render.com
3. **Connect GitHub** repository
4. **Deploy** using the steps above
5. **Share your URL** with friends!

Your chess server will be live and accessible online! 🎉

