package com.antsglobe.restcommerse.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.antsglobe.aeroquiz.NotificationAdapter
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.NavExpandableAdaptor
import com.antsglobe.restcommerse.databinding.ActivityHomeBinding
import com.antsglobe.restcommerse.databinding.HeaderDrawerBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.GetAdminTokenViewModel
import com.antsglobe.restcommerse.viewmodel.NotificationViewModel
import com.antsglobe.restcommerse.viewmodel.ProfileViewModel
import com.antsglobe.restcommerse.viewmodel.TokenViewModel
import com.antsglobe.restcommerse.viewmodel.VersionViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.antsglobe.restcommerse.viewmodel.WishlistViewmodel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Objects


class HomeActivity : AppCompatActivity(), WishListFragment.OnWishlistBadgeChangeListener {

    private lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    var binding: ActivityHomeBinding? = null
    private lateinit var navDrawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var headerBinding: HeaderDrawerBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var sharedPreferences: PreferenceManager

    private lateinit var viewModel: NotificationViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var tokenViewModel: TokenViewModel
    private lateinit var adminTokenViewModel: GetAdminTokenViewModel
    private lateinit var versionViewModel: VersionViewModel
    private lateinit var wishListViewModel: WishlistViewmodel
    private lateinit var notificationAdapter: NotificationAdapter
    private var NOTIFICATION_CHANNEL = "Testing"
    private val INTENT_PENDING = 101
    private val ALARM_INTERVAL = 10 * 1000
    private val ALARM_ID = 123

    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    var previousNotificationCount: String? = ""
    var currentNotificationCount: String? = ""

    var openNev = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        PreferenceManager.initialize(this)
        NetworkUtils.initialize(this)
        sharedPreferences = PreferenceManager(this)
        val email = sharedPreferences.getEmail().toString().trim()

        binding?.appBarDashboard?.bottomNavigationView?.selectedItemId = R.id.HomeFragment

        tokenViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[TokenViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[ProfileViewModel::class.java]

        adminTokenViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[GetAdminTokenViewModel::class.java]
        adminTokenViewModel.GetAdminTokenApiVM()
        adminTokeninitobserver()

        checkAndRequestNotificationPermission(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (!task.isSuccessful) {
                Log.e("TokenDetails", "Token Failed to received")
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("TokenDetails", token!!)

//            binding.tokenET.setText(token)

            tokenViewModel.getUserTokenVM(email, token)
            profileViewModel.getProfileVM(email)
            profileInitObserver()
        }

        setSupportActionBar(binding!!.appBarDashboard.toolbar)
        navDrawer = binding!!.navDrawer
        drawerLayout = binding!!.drawerLayout

        getNotificationPopup()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.getCurrentUser()
        updateUI(currentUser)


        navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupWithNavController(binding!!.appBarDashboard.bottomNavigationView, navController)

        wishlistBadge()

//        if (sharedPreferences.getMode() == true){
//            binding!!.appBarDashboard.bottomNavigationView.itemBackground=resources.getDrawable(R.color.blackfordark)
//        }

        if (!openNev) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


        binding!!.appBarDashboard.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.HomeFragment -> {
                    wishlistBadge()
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    notificationBadge()
                    true
                }

                R.id.MyWishListFragment -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.VISIBLE
                    binding!!.navDrawer.visibility = View.VISIBLE
                    true
                }

                R.id.bottom_menu_profile -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    true
                }

                R.id.notification -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    true
                }

                R.id.bottom_menu_navigation -> {


                    if (!openNev) {
                        openNev = true
                        customNavigation()

                    } else {
                        openNev = false
                        customNavigation()
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    }
                    true
                }

                else -> false
            }
            true
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.my_order -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.Tnc -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.PrivacyPolicy -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.RefundPolicy -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.ShippingPolicy -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.pay_now -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.aboutUs -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.menu_legal_policies -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.shop_by_category -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.Setting -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.ViewOfferFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.EditProfileFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.ProductDetailsFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.bottom_menu_my_cart -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.Product -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.ViewReviewDetails -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.ProductListFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.MostPopular -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.AllCategoryFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.AddressListFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.AddReview -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                R.id.MyWishListFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE

                }

                R.id.searchProductFragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE

                }

                R.id.ratingAndReview -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE

                }

                R.id.fragment_support -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE

                }

                R.id.faqs_fragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE

                }

                R.id.maps_fragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE

                }

                R.id.order_fragment -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    binding!!.navDrawer.visibility = View.GONE
                }

                else -> {
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun adminTokeninitobserver() {
        adminTokenViewModel.getAdminToken.observe(this){ response ->

            response?.content?.get(0)?.token
            Log.d("tokens",response?.content?.get(0)?.token.toString())
//            Toast.makeText(this@HomeActivity, "adminTokenViewModel ${response?.content?.get(0)?.token}", Toast.LENGTH_SHORT).show()

            sharedPreferences.setAdminToken(response?.content?.get(0)?.token)

        }

    }

    fun wishlistBadge() {
        wishListViewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[WishlistViewmodel::class.java]

        var wishlistBadge =
            binding!!.appBarDashboard.bottomNavigationView.getOrCreateBadge(R.id.MyWishListFragment)
        wishlistBadge.backgroundColor = ContextCompat.getColor(this, R.color.orange)

        wishListViewModel.getProductList(sharedPreferences.getEmail().toString())

        wishListViewModel.apiresponse.observe(this) {
            //  Toast.makeText(this, "${it.size}", Toast.LENGTH_SHORT).show()
            if (sharedPreferences.getMode() == true) {
                binding!!.appBarDashboard.bottomNavigationView.setBackgroundColor(
                    resources.getColor(
                        R.color.blackfordark
                    )
                )
                binding!!.appBarDashboard.bottomNavigationView.itemBackground =
                    resources.getDrawable(R.color.blackfordark)
            } else {
                binding!!.appBarDashboard.bottomNavigationView.setBackgroundColor(
                    resources.getColor(
                        R.color.whitefordark
                    )
                )
                binding!!.appBarDashboard.bottomNavigationView.itemBackground =
                    resources.getDrawable(R.color.whitefordark)
            }
            if (it?.size == 0) {

                wishlistBadge.isVisible = false
            } else {
                wishlistBadge.isVisible = true
            }
        }

    }

    var count: Int = 0

    @SuppressLint("MissingPermission")
    private fun getNotificationPopup() {

        if (sharedPreferences.getNotificationCount().toString() != "null") {
            count = Integer.parseInt(sharedPreferences.getNotificationCount().toString())
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[NotificationViewModel::class.java]

        versionViewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[VersionViewModel::class.java]

        val nm = NotificationManagerCompat.from(this)
        val iNotify = Intent(applicationContext, HomeActivity::class.java)
        iNotify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pi = PendingIntent.getActivity(
            this, INTENT_PENDING, iNotify,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        versionViewModel.getVersionVM()

        var versionName: Double = getString(R.string.appversion).toDouble()

        versionViewModel.getVersionItem.observe(this) {

            if (it[0].version_code.toDouble() <= versionName) {

            } else {
                showPopUpDialog(it[0].version_code)
            }
        }

        viewModel.getNotificationItem.observe(this) { notificationResp ->
//            LoadingDialog.dismissProgressDialog()

            sharedPreferences.setNotificationPreviousCount(notificationResp.size.toString())
            previousNotificationCount = sharedPreferences.getNotificationPreviousCount().toString()
            notificationBadge()

            for (notificationLoop in notificationResp) {
//                val notificationId =
//                    notificationLoop.Id.toInt()

                if (count < notificationLoop.ID.toInt()) {
                    val notificationBuilder: NotificationCompat.Builder =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            nm.createNotificationChannel(
                                NotificationChannel(
                                    NOTIFICATION_CHANNEL,
                                    "channelname",
                                    NotificationManager.IMPORTANCE_HIGH
                                )
                            )
                            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                                .setColorized(true)
                                .setColor(ContextCompat.getColor(this, android.R.color.black))
                                .setSmallIcon(R.drawable.notify_logo)
                                .setSubText(notificationLoop.title)
                                .setContentText(notificationLoop.description)
                                .setContentIntent(pi)
                        } else {
                            NotificationCompat.Builder(this)
                                .setColorized(true)
                                .setColor(ContextCompat.getColor(this, android.R.color.black))
                                .setSmallIcon(R.drawable.notify_logo)
                                .setSubText(notificationLoop.title)
                                .setContentText(notificationLoop.description)
                                .setContentIntent(pi)
                        }

                    val notification = notificationBuilder.build()
                    nm.notify(INTENT_PENDING, notification)
                }

                sharedPreferences.setNotificationCount(notificationLoop.ID.toString())

            }
        }
        viewModel.getAllNotifications()
    }


    fun isNotificationPermissionAllowed(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }


    // To check permission and show dialog if not allowed
    fun checkAndRequestNotificationPermission(context: Context) {
        if (!isNotificationPermissionAllowed(context)) {
            showPopUpDialog(context)
        }
    }

    private fun showPopUpDialog(context: Context) {
        popUpDialog = Dialog(this, R.style.popup_dialog)
        popUpDialog?.setContentView(R.layout.popup_dialogbox_notification)


        val window: Window? = popUpDialog!!.getWindow()
        if (window != null) {
            val params = window.attributes
            params.gravity = Gravity.BOTTOM
            params.dimAmount = 0.2f
            window.attributes = params
        }
        popUpDialog!!.show()
        Objects.requireNonNull<Window>(popUpDialog!!.getWindow())
            .setBackgroundDrawableResource(R.drawable.border_popup_bg)

        val productNameText: TextView = popUpDialog!!.findViewById(R.id.productName)
        val laterBtn: TextView = popUpDialog!!.findViewById(R.id.doneCart)
        val updateNowBtn: TextView = popUpDialog!!.findViewById(R.id.checkoutCart)

        laterBtn.setOnClickListener { popUpDialog!!.dismiss() }

        updateNowBtn.setOnClickListener {
            openNotificationSettings(context)
            popUpDialog!!.dismiss()
        }
    }

    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Settings.ACTION_APPLICATION_DETAILS_SETTINGS.toUri()
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("package", context.packageName)
            }
        }
        context.startActivity(intent)
    }



    @SuppressLint("MissingInflatedId")
    fun customNavigation() {
        /* val customContentView =
             layoutInflater.inflate(R.layout.naviagtion_drawer, navDrawer, false)
         navDrawer.addView(customContentView)*/
        drawerLayout.openDrawer(GravityCompat.END)
        binding!!.nevBarDrawer.username.text = sharedPreferences.getName()
        binding!!.nevBarDrawer.userPhone.text = sharedPreferences.getPhoneNo()
        binding!!.nevBarDrawer.userEmail.text = sharedPreferences.getEmail()

        // val androidVersion = Build.VERSION.SDK_INT
        val androidVersion = getString(R.string.appversion)
        binding!!.nevBarDrawer.tvAndriodVersion.text = "Version : ${androidVersion}"
//        binding.nevBarDrawer.userProfile.setImageResource(sharedPreferences.getProfilePic())

        binding!!.nevBarDrawer.ivWhatsapp.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            // openWhatsApp("9075539271")
        }

        binding!!.nevBarDrawer.ivInstagram.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            //  openInstagram("___trupti___06")
        }
        val profilePicName = sharedPreferences.getProfilePic()

        if (profilePicName != null) {

            val drawableMap = mapOf(
                "boy1" to R.drawable.boy1,
                "boy2" to R.drawable.boy2,
                "boy3" to R.drawable.boy3,
                "boy4" to R.drawable.boy4,
                "girl1" to R.drawable.girl1,
                "girl2" to R.drawable.girl2,
                "girl3" to R.drawable.girl3,
                "girl4" to R.drawable.girl4,
                "girl5" to R.drawable.girl5,
            )

            val drawableName = profilePicName

            val imageView = binding!!.nevBarDrawer.userProfile

            imageView.setImageResource(drawableMap[drawableName] ?: 0)

        } else {
            binding!!.nevBarDrawer.userProfile.setImageResource(R.drawable.boy1)
        }
        if (sharedPreferences.getMode() == true) {
            binding!!.nevBarDrawer.EditProfile.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.EditProfile.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.navDrawer.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvShopByCategory.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvViewOffer.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvMyAddress.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvRatingReview.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvSupport.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvAbout.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvFaq.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvSetting.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvShareApp.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvLogout.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvAndriodVersion.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.expandableListView.setBackgroundColor(resources.getColor(R.color.blackfordark))

        } else {
            binding!!.nevBarDrawer.EditProfile.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.EditProfile.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.navDrawer.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding!!.nevBarDrawer.tvShopByCategory.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvViewOffer.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvMyAddress.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvRatingReview.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvSupport.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvAbout.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvFaq.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvSetting.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvShareApp.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvLogout.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.tvAndriodVersion.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.nevBarDrawer.expandableListView.setBackgroundColor(resources.getColor(R.color.whitefordark))
        }

        binding!!.nevBarDrawer.EditProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.EditProfileFragment)
        }

        binding!!.nevBarDrawer.tvShopByCategory.setOnClickListener {
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.AllCategoryFragment)
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        binding!!.nevBarDrawer.tvViewOffer.setOnClickListener {
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.ViewOfferFragment)
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        /* binding!!.nevBarDrawer.tvMyAddress.setOnClickListener {
             navController.navigateUp() // to clear previous navigation history
             navController.navigate(R.id.AddressListFragment)
             drawerLayout.closeDrawer(GravityCompat.END)
         }*/

        binding!!.nevBarDrawer.tvRatingReview.setOnClickListener {
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.ratingAndReview)
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        binding!!.nevBarDrawer.tvSupport.setOnClickListener {
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.fragment_support)
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        binding!!.nevBarDrawer.tvAbout.setOnClickListener {
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.aboutUs)
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        binding!!.nevBarDrawer.tvFaq.setOnClickListener {
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.faqs_fragment)
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        binding!!.nevBarDrawer.tvSetting.setOnClickListener {
            navController.navigateUp() // to clear previous navigation history
            navController.navigate(R.id.Setting)
            drawerLayout.closeDrawer(GravityCompat.END)

            /* val intent = Intent(this, SettingsActivity::class.java)
             startActivity(intent)
             Activity().finish()*/
        }

        binding!!.nevBarDrawer.tvShareApp.setOnClickListener {

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plane")
            val shareSubApp = "RestCommerce"
            val shareAppLink = "https://play.google.com/store/apps/details?id=$packageName"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubApp)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
            startActivity(Intent.createChooser(shareIntent, "Share Options"))
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        binding!!.nevBarDrawer.tvLogout.setOnClickListener {
            showPopUpDialoglogout()
        }


        val expandableListView = binding!!.nevBarDrawer.expandableListView
        val listDataHeader = listOf("My Account")
        val listDataChild = hashMapOf(
            "My Account" to listOf(
                "My Orders",
                "My Profile",
                "My Addresses"
            )
        )
        val listAdapter = NavExpandableAdaptor(this, listDataHeader, listDataChild)
        expandableListView.setAdapter(listAdapter)
        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, itemId ->
            // Handle child item click here
            when (itemId) {
                0L -> {
                    navController.navigateUp() // to clear previous navigation history
                    navController.navigate(R.id.my_order)
                    true
                }

                1L -> {
                    val bundle = Bundle()
                    bundle.putBoolean("isNavigation", true)
                    navController.navigateUp() // to clear previous navigation history
                    navController.navigate(R.id.bottom_menu_profile, bundle)
                    binding!!.appBarDashboard.bottomNavigationView.visibility = View.GONE
                    true
                }

                2L -> {
                    navController.navigateUp() // to clear previous navigation history
                    navController.navigate(R.id.AddressListFragment)
                    drawerLayout.closeDrawer(GravityCompat.END)
                    true
                }

            }
            expandableListView.collapseGroup(0)
            drawerLayout.closeDrawer(GravityCompat.END)
            false
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                expandableListView.collapseGroup(0)
            }

            override fun onDrawerOpened(drawerView: View) {
                expandableListView.collapseGroup(0)
            }

            override fun onDrawerClosed(drawerView: View) {
                expandableListView.collapseGroup(0)
                openNev = false
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            override fun onDrawerStateChanged(newState: Int) {
                expandableListView.collapseGroup(0)
            }
        })
    }

    private fun customToast(context: Context, title: String, imageResourceId: Int) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast_layout, null)

        val imageViewIcon = layout.findViewById<ImageView>(R.id.imageViewIcon)
        imageViewIcon.setImageResource(imageResourceId) // Set the image using the passed resource ID

        val textViewMessage = layout.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.text = title

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    private fun openWhatsApp(phoneNumber: String) {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun openInstagram(username: String) {
        val uri = Uri.parse("http://instagram.com/_u/$username")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.instagram.android")
        startActivity(intent)
    }

    private fun profileInitObserver() {
        profileViewModel.getProfile.observe(this) { profile ->
//            LoadingDialog.dismissProgressDialog()
            if (profile?.status == "200") {

                profileViewModel.getProfileItem.observe(this) { profileData ->

                    Log.e("profileData", "profileData $profileData")
                    val name = profileData[0].name
                    val email = profileData[0].email
                    val phoneNo = profileData[0].mobno
                    val dob = profileData[0].dob
                    val address = profileData[0].address
                    val gender = profileData[0].gender
                    val profileImage = profileData[0].uer_image

                    val token = profileData[0].firebase_token

//                    Toast.makeText(this@HomeActivity, token, Toast.LENGTH_SHORT).show()

                    if (profileImage != null) {

                        sharedPreferences.setProfilePic(profileImage)
                        sharedPreferences.setAvatarImg(profileImage)

//                        val drawableMap = mapOf(
//                            "boy1" to R.drawable.boy1,
//                            "boy2" to R.drawable.boy2,
//                            "boy3" to R.drawable.boy3,
//                            "boy4" to R.drawable.boy4,
//                            "girl1" to R.drawable.girl1,
//                            "girl2" to R.drawable.girl2,
//                            "girl3" to R.drawable.girl3,
//                            "girl4" to R.drawable.girl4,
//                            "girl5" to R.drawable.girl5,
//                        )
//
//                        val drawableName = profileImage
//
//                        val imageView = binding.
//
//                        imageView.setImageResource(drawableMap[drawableName] ?: 0)
//
//                        sharedPreferences.setProfilePic(profileImage)
                    }

//                    sharedPreferences.setName(name)
//                    sharedPreferences.setEmail(email)
//                    sharedPreferences.setPhoneNo(phoneNo)
//                    sharedPreferences.setAccessToken(accessToken)
//                    sharedPreferences.setLoggedIn(true)

                }

            } else {
                customToast(this, "${profile?.message}", R.drawable.ic_info)
            }
        }
    }

    private fun googleSignOut() {
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign-out successful, clear user's data and update UI
//                updateUI(null)
            } else {
                // Sign-out failed, handle appropriately
                customToast(this, "Sign-out failed.", R.drawable.ic_info)

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    override fun onResume() {
        super.onResume()
        binding!!.appBarDashboard.bottomNavigationView.selectedItemId = R.id.HomeFragment
    }

    override fun onBackPressed() {
        notificationBadge()
        wishlistBadge()
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in
            Log.d(TAG, "User signed in: " + user.email)
            // You can update UI to show signed-in user details
        } else {
            // User is signed out
            Log.d(TAG, "User signed out")
            // You can update UI to show that user is signed out
        }
    }

    private var popUpDialog: Dialog? = null


    private fun showPopUpDialog(versionCode: String) {
        popUpDialog = Dialog(this, R.style.popup_dialog)

        if (sharedPreferences.getMode() == true) {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_version_dark)
        } else {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_version)
        }

//        val window: Window? = popUpDialog!!.getWindow()
//        if (window != null) {
//            val params = window.attributes
//            params.gravity = Gravity.BOTTOM
//            params.verticalMargin =
//                resources.getDimension(R.dimen.bottom_margin) / windowManager.defaultDisplay.height
//            params.dimAmount = 0.2f
//            window.attributes = params
//        }
        popUpDialog!!.show()
//        Objects.requireNonNull<Window>(popUpDialog!!.getWindow())
//            .setBackgroundDrawableResource(R.drawable.border_popup_bg)


        val versionCodeText: TextView = popUpDialog!!.findViewById(R.id.version)
        val laterBtn: TextView = popUpDialog!!.findViewById(R.id.laterBtn)
        val updateNowBtn: TextView = popUpDialog!!.findViewById(R.id.updateNowBtn)

        versionCodeText.text = "Version ${versionCode}"


        laterBtn.setOnClickListener {
            popUpDialog!!.dismiss()
        }

        updateNowBtn.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
            popUpDialog!!.dismiss()
        }

    }

    private fun showPopUpDialoglogout() {
        popUpDialog = Dialog(this, R.style.popup_dialog)

        if (sharedPreferences.getMode() == true) {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_logout_dark)
        } else {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_logout)
        }

//        val window: Window? = popUpDialog!!.getWindow()
//        if (window != null) {
//            val params = window.attributes
//            params.gravity = Gravity.BOTTOM
//            params.verticalMargin =
//                resources.getDimension(R.dimen.bottom_margin) / windowManager.defaultDisplay.height
//            params.dimAmount = 0.2f
//            window.attributes = params
//        }
        popUpDialog!!.show()
//        Objects.requireNonNull<Window>(popUpDialog!!.getWindow())
//            .setBackgroundDrawableResource(R.drawable.border_popup_bg)


        val no: TextView = popUpDialog!!.findViewById(R.id.no)
        val yes: TextView = popUpDialog!!.findViewById(R.id.yes)



        no.setOnClickListener {
            popUpDialog!!.dismiss()
        }

        yes.setOnClickListener {
            try {
                mAuth?.signOut()
                mGoogleSignInClient?.signOut()?.addOnCompleteListener(this,
                    OnCompleteListener<Void?> {
                        customToast(this, "Signed out successfully", R.drawable.ic_info)
                        updateUI(null)
                    })
                popUpDialog!!.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            } catch (e: ActivityNotFoundException) {
                Log.d("logout failed", e.message.toString())
            }
            popUpDialog!!.dismiss()
            drawerLayout.closeDrawer(GravityCompat.END)
//            sharedPreferences.clear().apply()
            PreferenceManager(this).clearAllData()


//            try {
//                // Clear application data
//                Runtime.getRuntime().exec("pm clear " + packageName)
//                restartApp()
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

        }


    }


    private fun restartApp() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onWishlistBadgeChanged(itemSize: String) {
        var wishlistBadge =
            binding!!.appBarDashboard.bottomNavigationView.getOrCreateBadge(R.id.MyWishListFragment)
        wishlistBadge.backgroundColor = ContextCompat.getColor(this, R.color.orange)
        //  Toast.makeText(this, "${it.size}", Toast.LENGTH_SHORT).show()
        if (itemSize == "0") {

            wishlistBadge.isVisible = false
        } else {
            wishlistBadge.isVisible = true
        }
    }

    fun notificationBadge() {
        //   Toast.makeText(this, "current count ${sharedPreferences.getNotificationBlinkCount()}", Toast.LENGTH_SHORT).show()
        //  Toast.makeText(this, "previous count ${previousNotificationCount}", Toast.LENGTH_SHORT).show()

        /*  Show the blink dot in the bottom navigation */
        var notificationBadge =
            binding!!.appBarDashboard.bottomNavigationView.getOrCreateBadge(R.id.notification)
        notificationBadge.backgroundColor = ContextCompat.getColor(this, R.color.orange)

        val currentNotificationCount = sharedPreferences.getNotificationBlinkCount()
        // val previousNotificationCount  = sharedPreferences.getNotificationPreviousCount().toString()
        if (previousNotificationCount == "0") {
            notificationBadge.isVisible = false
        } else {
            if (currentNotificationCount == previousNotificationCount) {
                notificationBadge.isVisible = false
            } else {
                notificationBadge.isVisible = true
            }
        }

    }

}