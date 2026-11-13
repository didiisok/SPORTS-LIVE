
package com.tiyu.zhibo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var rootLayout: FrameLayout
    private lateinit var loadingOverlay: FrameLayout

    private lateinit var jokeTextView: TextView
    private lateinit var jokeScrollView: ScrollView
    private lateinit var firstJokeGroup: LinearLayout

    private var jokeAnimator: ValueAnimator? = null
    private var exitFullscreen: (() -> Unit)? = null

    private val controlHandler = Handler(Looper.getMainLooper())

    private var homeReady = false

    // NBA 梗库：严格按照一条詹姆斯、一条其他球星交替排列
    private val nbaJokes = listOf(
        "正在联系詹姆斯：下一站目的地正在计算。",
        "正在联系亚历山大：中距离已经瞄准，防守人请注意。",
        "正在联系詹姆斯：411 纪录还在更新。",
        "正在联系恩比德：先申请一个犯规再说。",
        "正在联系詹姆斯：这球到底是谁的功劳？",
        "正在联系布伦森：纽约队长已就位，关键球交给我。",
        "正在联系詹姆斯：暂停一下，先看看数据统计。",
        "正在联系字母哥：希腊怪兽正在冲锋。",
        "正在联系詹姆斯：关键时刻，镜头准备好了。",
        "正在联系库里：超远三分正在加载。",
        "正在联系詹姆斯：队友已经准备好接球。",
        "正在联系杜兰特：死神即将上线。",
        "正在联系詹姆斯：赛后采访稿正在准备。",
        "正在联系约基奇：正在寻找队友，顺便找一下马儿。",
        "正在联系詹姆斯：历史地位讨论又开始了。",
        "正在联系东契奇：正在和裁判友好沟通。",
        "正在联系詹姆斯：比赛还没结束，纪录先刷新了。",
        "正在联系哈登：后撤步和造犯规模式启动。",
        "正在联系詹姆斯：詹蜜和詹黑即将进入直播间。",
        "正在联系爱德华兹：准备隔扣，请让开。",
        "正在联系詹姆斯：数据统计员已经开始加班。",
        "正在联系文班亚马：身高优势正在加载。",
        "正在联系詹姆斯：这项助攻也算我的贡献吧？",
        "正在联系利拉德：Logo 三分已进入射程。",
        "正在联系詹姆斯：比赛结束，纪录还没结束。",
        "正在联系威少：三双模式即将启动。",
        "正在联系詹姆斯：先看看队友有没有空位。",
        "正在联系欧文：篮球魔术即将开始。",
        "正在联系詹姆斯：镜头怎么又对准我了？",
        "正在联系巴特勒：季后赛模式已开启。",
        "正在联系詹姆斯：今天又是创造历史的一天。",
        "正在联系乐福：三分手感正在回归。",
        "正在联系詹姆斯：这项纪录还没人追上来。",
        "正在联系保罗·乔治：丝滑跳投正在加载。",
        "正在联系詹姆斯：队友们，关键球交给谁？",
        "正在联系卡哇伊：机器人正在启动。",
        "正在联系詹姆斯：先把数据刷到好看一点。",
        "正在联系德罗赞：中距离教学开始。",
        "正在联系詹姆斯：第四节才是真正的开始。",
        "正在联系莫兰特：弹跳高度突破限制。",
        "正在联系詹姆斯：这次又要刷新什么纪录？",
        "正在联系锡安：篮筐请做好准备。",
        "正在联系詹姆斯：比赛录像记得保存。",
        "正在联系特雷·杨：超远三分已发射。",
        "正在联系詹姆斯：队友已经准备好接球了。",
        "正在联系克莱：佛祖正在寻找手感。",
        "正在联系詹姆斯：关键球来了，镜头准备好。",
        "正在联系浓眉：防守大闸已经上线。",
        "正在联系詹姆斯：历史排名的话题又来了。",
        "正在联系科比：凌晨四点的训练还在继续。",
        "正在联系詹姆斯：这次庆祝动作准备好了。",
        "正在联系乔丹：正在准备最后一投。",
        "正在联系詹姆斯：先别急，比赛还没结束。",
        "正在联系奥尼尔：篮筐维修人员已出发。",
        "正在联系詹姆斯：数据面板又要更新了。",
        "正在联系艾弗森：正在寻找 crossover。",
        "正在联系詹姆斯：詹蜜已经开始庆祝了。",
        "正在联系姚明：正在切换中文解说。",
        "正在联系詹姆斯：詹黑评论区正在排队。",
        "正在联系韦德：闪电侠即将登场。",
        "正在联系詹姆斯：下一场继续冲击纪录。",
        "正在联系麦迪：35 秒奇迹准备中。"
    )

    private val revealHomeRunnable = Runnable {
        if (::webView.isInitialized && ::loadingOverlay.isInitialized) {
            webView.visibility = View.VISIBLE
            loadingOverlay.visibility = View.GONE
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    // 隐藏状态栏和底部导航栏
    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    // 创建白色启动画面
    private fun createLoadingScreen() {

        loadingOverlay = FrameLayout(this).apply {
            setBackgroundColor(Color.WHITE)
            elevation = dp(30).toFloat()
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(12), dp(24), dp(12))
        }

        val appIcon = ImageView(this).apply {
            setImageDrawable(packageManager.getApplicationIcon(packageName))
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        content.addView(
            appIcon,
            LinearLayout.LayoutParams(dp(82), dp(82)).apply {
                bottomMargin = dp(10)
            }
        )

        val title = TextView(this).apply {
            text = "SPORTS LIVE"
            textSize = 29f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        content.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this).apply {
            text = "全球赛事 · 实时直播"
            textSize = 14f
            setTextColor(0xFF666666.toInt())
            gravity = Gravity.CENTER
            letterSpacing = 0.08f
        }

        content.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(7)
                bottomMargin = dp(18)
            }
        )

        val connectText = TextView(this).apply {
            text = "正在连接赛事服务器"
            textSize = 15f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        content.addView(
            connectText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val loadingText = TextView(this).apply {
            text = "正在加载赛事首页..."
            textSize = 14f
            setTextColor(0xFF777777.toInt())
            gravity = Gravity.CENTER
        }

        content.addView(
            loadingText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(7)
                bottomMargin = dp(14)
            }
        )

        val line = View(this).apply {
            setBackgroundColor(0xFFE0E0E0.toInt())
        }

        content.addView(
            line,
            LinearLayout.LayoutParams(dp(180), dp(1)).apply {
                bottomMargin = dp(10)
            }
        )

        val tickerContainer = FrameLayout(this)

        jokeScrollView = ScrollView(this).apply {
            isVerticalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            clipToPadding = true
            setPadding(0, 0, 0, 0)
        }

        val tickerContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        firstJokeGroup = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val secondJokeGroup = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        nbaJokes.forEach { joke ->
            firstJokeGroup.addView(createJokeText(joke))
            secondJokeGroup.addView(createJokeText(joke))
        }

        tickerContent.addView(firstJokeGroup)
        tickerContent.addView(secondJokeGroup)

        jokeScrollView.addView(tickerContent)

        tickerContainer.addView(
            jokeScrollView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        jokeTextView = TextView(this).apply {
            text = "正在联系詹黑上线评论..."
            textSize = 18.2f
            setTextColor(0xFF333333.toInt())
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            letterSpacing = 0.03f
            visibility = View.GONE
        }

        tickerContainer.addView(
            jokeTextView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        content.addView(
            tickerContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(220)
            )
        )

        loadingOverlay.addView(
            content,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )
    }

    private fun createJokeText(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(0xFF444444.toInt())
            gravity = Gravity.CENTER
            setPadding(dp(5), dp(2), dp(5), dp(2))
            minHeight = dp(30)
        }
    }

    // 开始连续向上滚动
    private fun startJokeTicker() {

        jokeAnimator?.cancel()
        jokeAnimator = null

        jokeScrollView.visibility = View.VISIBLE
        jokeTextView.visibility = View.GONE
        jokeScrollView.scrollTo(0, 0)

        jokeScrollView.post {

            if (homeReady || loadingOverlay.visibility != View.VISIBLE) {
                return@post
            }

            val cycleHeight = firstJokeGroup.height

            if (cycleHeight <= 0) {
                return@post
            }

            val duration = (cycleHeight * 18L).coerceAtLeast(10000L)

            val animator = ValueAnimator.ofInt(0, cycleHeight).apply {
                this.duration = duration
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()

                addUpdateListener {
                    jokeScrollView.scrollTo(0, it.animatedValue as Int)
                }
            }

            jokeAnimator = animator
            animator.start()
        }
    }

    private fun stopJokeTicker() {
        jokeAnimator?.cancel()
        jokeAnimator = null
    }

    private fun showLoadingScreen() {

        homeReady = false

        controlHandler.removeCallbacks(revealHomeRunnable)
        stopJokeTicker()

        webView.visibility = View.INVISIBLE
        loadingOverlay.visibility = View.VISIBLE

        jokeTextView.visibility = View.GONE
        jokeScrollView.visibility = View.VISIBLE
        jokeScrollView.scrollTo(0, 0)

        startJokeTicker()
    }

    // 首页 12 个链接清理成功后调用
    private fun onHomeReady() {

        if (homeReady) return

        homeReady = true

        stopJokeTicker()
        controlHandler.removeCallbacks(revealHomeRunnable)

        jokeScrollView.visibility = View.GONE

        jokeTextView.text = "正在联系詹黑上线评论..."
        jokeTextView.visibility = View.VISIBLE

        controlHandler.postDelayed(revealHomeRunnable, 2000)
    }

    private inner class HomePageBridge {
        @JavascriptInterface
        fun homeReady() {
            runOnUiThread {
                onHomeReady()
            }
        }
    }

    // 全屏视频容器：拦截双指缩放
    private inner class VideoContainer(
        private val onScreenTap: () -> Unit
    ) : FrameLayout(this@MainActivity) {

        private var blockMultiTouch = false

        override fun dispatchTouchEvent(event: MotionEvent): Boolean {

            if (event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
                blockMultiTouch = true

                val cancelEvent = MotionEvent.obtain(event)
                cancelEvent.action = MotionEvent.ACTION_CANCEL
                super.dispatchTouchEvent(cancelEvent)
                cancelEvent.recycle()

                return true
            }

            if (blockMultiTouch) {
                if (
                    event.actionMasked == MotionEvent.ACTION_UP ||
                    event.actionMasked == MotionEvent.ACTION_CANCEL
                ) {
                    blockMultiTouch = false
                }
                return true
            }

            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                onScreenTap()
            }

            return super.dispatchTouchEvent(event)
        }
    }

    // 屏蔽直播页面底部 APP 下载广告和网址公告弹窗
    private val bottomAdJs = """
        (function() {
            if (window.__bottomAdCleanerStarted) return;
            window.__bottomAdCleanerStarted = true;

            // 精准清理“看球网址发布页，收藏不迷路”弹窗
            function cleanNoticePopup() {
                const normalize = s =>
                    (s || "").replace(/\s+/g, "").trim();

                const all = Array.from(
                    document.querySelectorAll("body *")
                );

                const headings = all.filter(el => {
                    const text = normalize(
                        el.innerText || el.textContent
                    );

                    return text === "看球网址发布页，收藏不迷路";
                });

                headings.forEach(heading => {
                    let target = heading;

                    // 从标题向上查找真正包含整个公告的弹窗容器
                    for (let i = 0; i < 10 && target; i++) {
                        if (
                            target === document.body ||
                            target === document.documentElement
                        ) break;

                        const text = normalize(
                            target.innerText || target.textContent
                        );

                        const links = Array.from(
                            target.querySelectorAll("a[href]")
                        );

                        const urlMatches =
                            text.match(/(?:https?:\/\/|www\.)[^\s]+/gi) || [];

                        const urlCount = Math.max(
                            links.length,
                            urlMatches.length
                        );

                        const buttons = Array.from(
                            target.querySelectorAll(
                                "button,a,[role='button'],input,[onclick],div,span"
                            )
                        );

                        const hasConfirmButton = buttons.some(el => {
                            const buttonText = normalize(
                                el.innerText || el.textContent || el.value
                            );

                            return buttonText === "我知道了";
                        });

                        // 必须同时满足公告标题、两个网址和确认按钮
                        // 才会隐藏，避免误伤直播线路和其他页面元素
                        if (
                            text.includes("看球网址发布页，收藏不迷路") &&
                            urlCount >= 2 &&
                            hasConfirmButton &&
                            text.length < 2000
                        ) {
                            target.style.setProperty(
                                "display", "none", "important"
                            );

                            target.style.setProperty(
                                "visibility", "hidden", "important"
                            );

                            target.style.setProperty(
                                "pointer-events", "none", "important"
                            );

                            break;
                        }

                        target = target.parentElement;
                    }
                });
            }

            // 保留原来的底部 APP 下载广告清理功能
            function cleanBottomAd() {
                const elements = Array.from(
                    document.querySelectorAll("body *")
                );

                elements.forEach(el => {
                    if (!el || !el.isConnected) return;

                    const text = (el.innerText || el.textContent || "")
                        .replace(/\s+/g, "");

                    if (
                        !text.includes("看球直播APP") ||
                        !text.includes("马上下载")
                    ) return;

                    let target = el;

                    for (let i = 0; i < 7 && target.parentElement; i++) {
                        const style = getComputedStyle(target);
                        const rect = target.getBoundingClientRect();

                        const fixedBottom =
                            (style.position === "fixed" ||
                             style.position === "sticky") &&
                            rect.bottom >= window.innerHeight - 120 &&
                            rect.top >= window.innerHeight * 0.55;

                        const bottomContainer =
                            rect.bottom >= window.innerHeight - 80 &&
                            rect.top >= window.innerHeight * 0.55 &&
                            rect.height <= window.innerHeight * 0.4;

                        if (fixedBottom || bottomContainer) break;

                        target = target.parentElement;
                    }

                    const style = getComputedStyle(target);
                    const rect = target.getBoundingClientRect();

                    const isBottomAd =
                        (style.position === "fixed" ||
                         style.position === "sticky" ||
                         rect.bottom >= window.innerHeight - 80) &&
                        rect.top >= window.innerHeight * 0.55 &&
                        rect.height <= window.innerHeight * 0.4;

                    if (isBottomAd) {
                        target.style.setProperty(
                            "display", "none", "important"
                        );

                        target.style.setProperty(
                            "visibility", "hidden", "important"
                        );
                    }
                });
            }

            function cleanAllAds() {
                cleanNoticePopup();
                cleanBottomAd();
            }

            cleanAllAds();

            const observer = new MutationObserver(cleanAllAds);

            observer.observe(document.documentElement, {
                childList: true,
                subtree: true,
                characterData: true
            });

            window.__bottomAdObserver = observer;
            window.__bottomAdTimer = setInterval(cleanAllAds, 1500);
        })();
    """.trimIndent()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        hideSystemBars()

        // 原生容器和 WebView 统一使用白色背景
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.WHITE)
        }

        webView = WebView(this).apply {
            setBackgroundColor(Color.WHITE)
            visibility = View.INVISIBLE
        }

        rootLayout.addView(
            webView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        createLoadingScreen()

        rootLayout.addView(
            loadingOverlay,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(rootLayout)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false

            loadWithOverviewMode = true
            useWideViewPort = true

            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false

            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
        }

        webView.addJavascriptInterface(HomePageBridge(), "AndroidHome")

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: android.graphics.Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)

                val pageUrl = url.substringBefore("?")
                    .substringBefore("#")
                    .trimEnd('/')

                if (pageUrl == "https://www.88kq.cc") {
                    showLoadingScreen()
                } else {
                    stopJokeTicker()
                    controlHandler.removeCallbacks(revealHomeRunnable)

                    // 页面加载期间使用白色底色，完成后再显示
                    loadingOverlay.visibility = View.GONE
                    rootLayout.setBackgroundColor(Color.WHITE)
                    view.setBackgroundColor(Color.WHITE)
                    view.visibility = View.INVISIBLE
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }

            override fun onPageFinished(
                view: WebView,
                url: String
            ) {
                super.onPageFinished(view, url)

                val pageUrl = url.substringBefore("?")
                    .substringBefore("#")
                    .trimEnd('/')

                if (pageUrl == "https://www.88kq.cc") {

                    val js = """
                        (function() {
                            const allowed = [
                                "泡泡直播官网",
                                "备用网址①",
                                "备用网址②",
                                "备用网址③",
                                "备用网址④",
                                "备用网址⑤",
                                "备用网址⑥",
                                "备用网址⑦",
                                "备用网址⑧",
                                "备用网址⑨",
                                "备用网址⑩",
                                "备用网址⑪"
                            ];

                            if (window.__homeCleanTimer) {
                                clearInterval(window.__homeCleanTimer);
                            }

                            window.__homeCleanTimer = setInterval(function() {

                                const links = Array.from(
                                    document.querySelectorAll("a")
                                );

                                const linkMap = new Map();

                                links.forEach(a => {
                                    const text = (a.innerText || a.textContent || "")
                                        .replace(/\s+/g, "")
                                        .trim();

                                    if (allowed.includes(text) && !linkMap.has(text)) {
                                        linkMap.set(text, a);
                                    }
                                });

                                const allFound = allowed.every(
                                    label => linkMap.has(label)
                                );

                                if (!allFound) return;

                                clearInterval(window.__homeCleanTimer);
                                window.__homeCleanTimer = null;

                                // 首页统一白色背景
                                document.documentElement.style.background = "#FFFFFF";
                                document.body.style.background = "#FFFFFF";
                                document.body.style.margin = "0";
                                document.body.style.padding = "0";
                                document.body.style.minHeight = "100vh";
                                document.body.style.fontFamily =
                                    "-apple-system, BlinkMacSystemFont, Roboto, sans-serif";

                                const wrapper = document.createElement("div");

                                Object.assign(wrapper.style, {
                                    width: "100%",
                                    maxWidth: "560px",
                                    minHeight: "100vh",
                                    boxSizing: "border-box",
                                    margin: "0 auto",
                                    padding: "30px 18px 24px",
                                    display: "flex",
                                    flexDirection: "column",
                                    background: "#FFFFFF"
                                });

                                const brand = document.createElement("div");

                                Object.assign(brand.style, {
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "space-between",
                                    marginBottom: "30px"
                                });

                                const brandName = document.createElement("div");
                                brandName.textContent = "SPORTS LIVE";

                                Object.assign(brandName.style, {
                                    color: "#171717",
                                    fontSize: "23px",
                                    fontWeight: "900",
                                    letterSpacing: "2px"
                                });

                                const live = document.createElement("span");
                                live.textContent = "● LIVE";

                                Object.assign(live.style, {
                                    color: "#168B55",
                                    background: "#E9F8F0",
                                    border: "1px solid #B9E8CE",
                                    borderRadius: "20px",
                                    padding: "6px 12px",
                                    fontSize: "11px",
                                    fontWeight: "bold",
                                    letterSpacing: "1px"
                                });

                                brand.appendChild(brandName);
                                brand.appendChild(live);
                                wrapper.appendChild(brand);

                                const heading = document.createElement("div");

                                const headingTitle = document.createElement("div");
                                headingTitle.textContent = "赛事直播导航";

                                Object.assign(headingTitle.style, {
                                    color: "#171717",
                                    fontSize: "27px",
                                    fontWeight: "800",
                                    letterSpacing: "1px",
                                    marginBottom: "8px"
                                });

                                const headingSub = document.createElement("div");
                                headingSub.textContent = "GLOBAL SPORTS · LIVE STREAMING";

                                Object.assign(headingSub.style, {
                                    color: "#777777",
                                    fontSize: "11px",
                                    letterSpacing: "2px",
                                    fontWeight: "600"
                                });

                                heading.appendChild(headingTitle);
                                heading.appendChild(headingSub);
                                wrapper.appendChild(heading);

                                function addDivider() {
                                    const line = document.createElement("div");

                                    Object.assign(line.style, {
                                        height: "1px",
                                        width: "100%",
                                        background: "#E5E7EB",
                                        margin: "25px 0 20px"
                                    });

                                    wrapper.appendChild(line);
                                }

                                addDivider();

                                const officialHeading = document.createElement("div");

                                Object.assign(officialHeading.style, {
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "space-between",
                                    marginBottom: "12px"
                                });

                                const officialLabel = document.createElement("span");
                                officialLabel.textContent = "官方主站";

                                Object.assign(officialLabel.style, {
                                    color: "#222222",
                                    fontSize: "16px",
                                    fontWeight: "bold"
                                });

                                const recommend = document.createElement("span");
                                recommend.textContent = "推荐";

                                Object.assign(recommend.style, {
                                    color: "#93620B",
                                    background: "#FFF6DF",
                                    border: "1px solid #F1D99D",
                                    borderRadius: "20px",
                                    padding: "4px 11px",
                                    fontSize: "11px",
                                    fontWeight: "bold"
                                });

                                officialHeading.appendChild(officialLabel);
                                officialHeading.appendChild(recommend);
                                wrapper.appendChild(officialHeading);

                                function styleCard(a, official) {
                                    Object.assign(a.style, {
                                        display: "flex",
                                        flexDirection: "column",
                                        justifyContent: "center",
                                        boxSizing: "border-box",
                                        textDecoration: "none",
                                        borderRadius: official ? "18px" : "14px",
                                        border: official
                                            ? "1px solid #C9DDF4"
                                            : "1px solid #E1E5EA",
                                        background: official
                                            ? "linear-gradient(135deg, #F1F7FF, #FFFFFF)"
                                            : "linear-gradient(145deg, #FFFFFF, #F8FAFC)",
                                        boxShadow: official
                                            ? "0 6px 20px rgba(40, 90, 150, 0.09)"
                                            : "0 3px 12px rgba(30, 50, 70, 0.05)",
                                        padding: official ? "21px 20px" : "16px 13px",
                                        minHeight: official ? "100px" : "78px",
                                        width: "100%",
                                        WebkitTapHighlightColor: "transparent"
                                    });
                                }

                                const officialLink = linkMap.get("泡泡直播官网");

                                styleCard(officialLink, true);

                                const officialTitle = document.createElement("span");
                                officialTitle.textContent = "直播官网";

                                Object.assign(officialTitle.style, {
                                    color: "#172B45",
                                    fontSize: "19px",
                                    fontWeight: "800",
                                    marginBottom: "8px"
                                });

                                const officialDesc = document.createElement("span");
                                officialDesc.textContent = "进入官方赛事直播页面";

                                Object.assign(officialDesc.style, {
                                    color: "#68788A",
                                    fontSize: "13px"
                                });

                                const officialArrow = document.createElement("span");
                                officialArrow.textContent = "↗";

                                Object.assign(officialArrow.style, {
                                    color: "#315F91",
                                    fontSize: "20px",
                                    alignSelf: "flex-end",
                                    marginTop: "-22px"
                                });

                                officialLink.replaceChildren(
                                    officialTitle,
                                    officialDesc,
                                    officialArrow
                                );

                                wrapper.appendChild(officialLink);

                                const backupHeading = document.createElement("div");
                                backupHeading.textContent = "备用线路";

                                Object.assign(backupHeading.style, {
                                    color: "#222222",
                                    fontSize: "16px",
                                    fontWeight: "bold",
                                    marginTop: "28px",
                                    marginBottom: "13px"
                                });

                                wrapper.appendChild(backupHeading);

                                const grid = document.createElement("div");

                                Object.assign(grid.style, {
                                    display: "grid",
                                    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
                                    gap: "12px"
                                });

                                const numbers = [
                                    "①","②","③","④","⑤","⑥",
                                    "⑦","⑧","⑨","⑩","⑪"
                                ];

                                for (let i = 1; i <= 11; i++) {
                                    const number = numbers[i - 1];
                                    const link = linkMap.get("备用网址" + number);

                                    styleCard(link, false);

                                    const title = document.createElement("span");
                                    title.textContent = "备用网址 " + number;

                                    Object.assign(title.style, {
                                        color: "#222222",
                                        fontSize: "14px",
                                        fontWeight: "bold",
                                        marginBottom: "7px"
                                    });

                                    const desc = document.createElement("span");
                                    desc.textContent = "备用访问入口";

                                    Object.assign(desc.style, {
                                        color: "#777777",
                                        fontSize: "11px"
                                    });

                                    link.replaceChildren(title, desc);
                                    grid.appendChild(link);
                                }

                                wrapper.appendChild(grid);

                                const footer = document.createElement("div");
                                footer.textContent = "SPORTS LIVE · LEBRON HATERS ONLY";

                                Object.assign(footer.style, {
                                    color: "#888888",
                                    fontSize: "10px",
                                    letterSpacing: "1.3px",
                                    textAlign: "center",
                                    padding: "30px 0 8px",
                                    marginTop: "auto"
                                });

                                wrapper.appendChild(footer);

                                document.body.replaceChildren(wrapper);

                                if (window.AndroidHome) {
                                    window.AndroidHome.homeReady();
                                }

                            }, 500);
                        })();
                    """.trimIndent()

                    view.evaluateJavascript(js, null)

                } else {

                    // 不再强制修改网页颜色，只清理底部广告
                    view.evaluateJavascript(bottomAdJs) {

                        val currentUrl = view.url
                            ?.substringBefore("?")
                            ?.substringBefore("#")
                            ?.trimEnd('/')

                        val finishedUrl = url
                            .substringBefore("?")
                            .substringBefore("#")
                            .trimEnd('/')

                        if (currentUrl == finishedUrl) {
                            view.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null
            private var popupWebView: WebView? = null

            private var controlPanel: LinearLayout? = null
            private var fullscreenContainer: VideoContainer? = null

            private val hideControlsRunnable = Runnable {
                controlPanel?.visibility = View.GONE
            }

            private fun showControls() {
                val panel = controlPanel ?: return

                panel.visibility = View.VISIBLE

                controlHandler.removeCallbacks(hideControlsRunnable)

                controlHandler.postDelayed(hideControlsRunnable, 3000)
            }

            private fun makeButton(text: String): Button {
                return Button(this@MainActivity).apply {
                    this.text = text
                    textSize = 11f
                    setTextColor(Color.WHITE)

                    isAllCaps = false
                    minWidth = 0
                    minimumWidth = 0
                    minHeight = 0
                    minimumHeight = 0

                    setPadding(dp(9), dp(4), dp(9), dp(4))

                    background = GradientDrawable().apply {
                        setColor(0xCC263238.toInt())
                        cornerRadius = dp(18).toFloat()
                        setStroke(dp(1), 0x99FFFFFF.toInt())
                    }
                }
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: android.os.Message
            ): Boolean {

                val newWebView = WebView(this@MainActivity).apply {
                    setBackgroundColor(Color.WHITE)
                }

                newWebView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }

                newWebView.webViewClient = object : WebViewClient() {

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {

                        webView.loadUrl(request.url.toString())
                        return true
                    }
                }

                popupWebView = newWebView

                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()

                return true
            }

            override fun onCloseWindow(window: WebView) {
                window.stopLoading()
                window.destroy()

                if (popupWebView === window) {
                    popupWebView = null
                }
            }

            // 进入视频全屏
            override fun onShowCustomView(
                view: View,
                callback: CustomViewCallback
            ) {

                if (customView != null) {
                    callback.onCustomViewHidden()
                    return
                }

                customView = view
                customViewCallback = callback

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                webView.visibility = View.GONE

                view.scaleX = 1f
                view.scaleY = 1f
                view.translationX = 0f
                view.translationY = 0f

                val container = VideoContainer {
                    showControls()
                }.apply {
                    setBackgroundColor(Color.BLACK)
                    clipChildren = false
                    clipToPadding = false
                    fitsSystemWindows = false
                }

                fullscreenContainer = container

                container.addView(
                    view,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                    )
                )

                val panel = LinearLayout(this@MainActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER

                    setPadding(dp(4), dp(2), dp(4), dp(2))

                    background = GradientDrawable().apply {
                        setColor(0xBB20252B.toInt())
                        cornerRadius = dp(24).toFloat()
                        setStroke(dp(1), 0x66FFFFFF.toInt())
                    }

                    elevation = dp(20).toFloat()
                }

                controlPanel = panel

                val originalButton = makeButton("原始尺寸")
                val zoomButton = makeButton("1.25 倍")

                panel.addView(originalButton)

                panel.addView(
                    zoomButton,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = dp(4)
                    }
                )

                originalButton.setOnClickListener {
                    view.scaleX = 1f
                    view.scaleY = 1f
                    view.translationX = 0f
                    view.translationY = 0f
                    showControls()
                }

                zoomButton.setOnClickListener {
                    view.scaleX = 1.25f
                    view.scaleY = 1.25f
                    view.translationX = 0f
                    view.translationY = 0f
                    showControls()
                }

                container.addView(
                    panel,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP or Gravity.END
                    ).apply {
                        topMargin = dp(16)
                        marginEnd = dp(16)
                    }
                )

                setContentView(container)
                hideSystemBars()

                container.post {
                    showControls()
                }

                exitFullscreen = {
                    onHideCustomView()
                }
            }

            // 退出视频全屏
            override fun onHideCustomView() {

                controlHandler.removeCallbacks(hideControlsRunnable)

                customView?.let { view ->
                    (view.parent as? ViewGroup)?.removeView(view)

                    view.scaleX = 1f
                    view.scaleY = 1f
                    view.translationX = 0f
                    view.translationY = 0f
                }

                fullscreenContainer?.removeAllViews()
                fullscreenContainer = null

                controlPanel = null
                customView = null

                requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                customViewCallback?.onCustomViewHidden()
                customViewCallback = null

                exitFullscreen = null

                setContentView(rootLayout)

                webView.visibility = View.VISIBLE
                hideSystemBars()
            }
        }

        if (savedInstanceState == null) {
            showLoadingScreen()
            webView.loadUrl("https://www.88kq.cc/")
        } else {
            webView.restoreState(savedInstanceState)
        }

        // 返回键优先退出视频全屏
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (exitFullscreen != null) {
                        exitFullscreen?.invoke()

                    } else if (webView.canGoBack()) {
                        webView.goBack()

                    } else {
                        finish()
                    }
                }
            }
        )
    }

    // 重新获得焦点时隐藏系统栏
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            hideSystemBars()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        controlHandler.removeCallbacksAndMessages(null)
        stopJokeTicker()

        if (::webView.isInitialized) {
            webView.destroy()
        }

        super.onDestroy()
    }
}
