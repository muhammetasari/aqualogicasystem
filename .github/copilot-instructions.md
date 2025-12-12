# GitHub Copilot Instructions - İzsu Projesi

Bu belge, İzsu Android uygulaması için GitHub Copilot'ın kod önerilerinde kullanacağı kuralları ve standartları tanımlar.

## 🎯 Proje Genel Bakış

İzsu, Jetpack Compose ve Material Design 3 kullanan modern bir Android uygulamasıdır. Firebase Authentication entegrasyonu ve çoklu dil desteği içerir.

## 📋 Genel Kodlama Kuralları

### Kotlin Standartları
- **Kotlin Coding Conventions**'a tam uyum sağla
- Değişken isimleri `camelCase` formatında olmalı
- Sınıf isimleri `PascalCase` formatında olmalı
- Sabitler `UPPER_SNAKE_CASE` formatında olmalı
- Paket isimleri tamamen küçük harf olmalı

### Null Safety
- Mümkün olduğunca nullable olmayan tipler (`String`, `Int`) kullan
- Null kontrollerinde elvis operator (`? :`) tercih et
- Safe call operator (`?.`) kullan
- `!!` operatörünü yalnızca kesinlikle gerekli durumlarda kullan

```kotlin
// İyi ✅
val name = user?.fullName ?: "Misafir"

// Kötü ❌
val name = user!! .fullName
```

## 🏗️ Mimari ve Yapı

### Katmanlı Mimari
Proje aşağıdaki katmanları takip eder:
- **UI Layer:** `ui/screens`, `ui/components`, `ui/theme`, `ui/common`
- **ViewModel Layer:** `ui/viewmodel`
- **Data Layer:** `data/model`, `data/repository`
- **Navigation:** `navigation`
- **Utils:** `util`, `utils`

### Paket Yapısı
```
com.aqualogicasystem.izsu/
├── data/
│   ├── model/          # Veri modelleri
│   └── repository/     # Repository sınıfları
├── navigation/         # Navigation Graph'lar
├── ui/
│   ├── common/         # Standart layout bileşenleri
│   ├── components/     # Yeniden kullanılabilir bileşenler
│   ├── screens/        # Ekran Composable'ları
│   ├── theme/          # Tema ve stil tanımları
│   └── viewmodel/      # ViewModel sınıfları
├── util/              # Yardımcı sınıflar
└── utils/             # Utility fonksiyonlar
```

## 🎨 Jetpack Compose Kuralları

### Composable Fonksiyonlar
- Composable fonksiyon isimleri PascalCase olmalı
- Her Composable için `@Preview` annotasyonu ekle Mocklanması gereken yerleri örneğin Firebase gibi alanları mock ile geçiştir.
- Modifier parametresini her zaman ilk parametre olarak al
- State hoisting prensibini uygula

```kotlin
// İyi ✅
@Composable
fun UserProfile(
    modifier: Modifier = Modifier,
    user: User,
    onLogout: () -> Unit
) {
    // İçerik
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    IzsuAppTheme {
        UserProfile(
            user = User("1", "Test", "test@example.com", null),
            onLogout = {}
        )
    }
}
```

### State Management
- `remember` ile lokal state yönet
- `rememberSaveable` ile configuration değişikliklerinde state'i koru
- ViewModel'den gelen state'leri `collectAsState()` ile topla
- Yan etkiler için `LaunchedEffect`, `DisposableEffect` kullan

```kotlin
// İyi ✅
val authState by viewModel.authState.collectAsState()
var showDialog by remember { mutableStateOf(false) }
```

### Material Design 3
- Tüm renkler için `MaterialTheme. colorScheme` kullan
- Typography için `MaterialTheme.typography` kullan
- Spacing için `MaterialTheme.shapes` kullan
- Özel renkler yerine tema renklerini tercih et

```kotlin
// İyi ✅
Text(
    text = "Başlık",
    style = MaterialTheme.typography.headlineLarge,
    color = MaterialTheme. colorScheme.primary
)

// Kötü ❌
Text(
    text = "Başlık",
    fontSize = 32.sp,
    color = Color(0xFF6200EE)
)
```

## 📐 Standart Layout Bileşenleri

### YENİ EKRAN OLUŞTURURKEN ZORUNLU KURALLAR

**Her yeni ekran için `StandardLayout`, `StandartTopAppBar` ve `StandardBottomBar` bileşenlerini kullan.**

### StandardLayout Kullanımı
Yeni bir ekran oluştururken **mutlaka** `StandardLayout` bileşenini kullan.  Bu bileşen otomatik olarak TopBar ve BottomBar'ı yönetir.

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YeniEkran(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    StandardLayout(
        navController = navController,
        title = "Ekran Başlığı",
        showTopBar = true,              // TopBar gösterilsin mi? 
        showBackButton = true,           // Geri butonu gösterilsin mi? 
        showBottomBar = true,            // BottomBar gösterilsin mi?
        onNavigateBack = { navController.popBackStack() },
        topAppBarActions = {             // Sağ üst köşe butonları (opsiyonel)
            IconButton(onClick = { /* Aksiyon */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
            }
        },
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    ) { paddingValues ->
        // Ekran içeriği buraya gelir
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // İçerik
        }
    }
}
```

### StandartTopAppBar Kullanımı
Eğer özel bir TopBar tasarımı gerekiyorsa, `StandartTopAppBar` kullan:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OzelTopBarluEkran(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            StandartTopAppBar(
                title = "Özel Başlık",
                showBackButton = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { /* Paylaş */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Paylaş")
                    }
                    IconButton(onClick = { /* Favorile */ }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorile")
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        // İçerik
    }
}
```

### StandardBottomBar
BottomBar otomatik olarak `StandardLayout` içinde yönetilir.  Manuel kullanım gerekmez, ancak gerekirse:

```kotlin
Scaffold(
    bottomBar = {
        StandardBottomBar(navController = navController)
    }
) { paddingValues ->
    // İçerik
}
```

### Ekran Tipleri ve Layout Konfigürasyonları

#### 1. Ana Sayfa (Home Screen)
```kotlin
StandardLayout(
    onNavigateBack = { /* Ana sayfa, geri gitmez */ },
    topAppBarTitle = "İzsu",
    showTopBar = false,        // Ana sayfada genelde TopBar gösterilmez
    showBackButton = false,
    showBottomBar = true,      // BottomBar gösterilir
    navController = navController
) { paddingValues ->
    // Ana sayfa içeriği
}
```

#### 2. Detay Sayfaları
```kotlin
StandardLayout(
    onNavigateBack = { navController.popBackStack() },
    topAppBarTitle = "Detay Başlığı",
    showTopBar = true,
    showBackButton = true,     // Geri butonu zorunlu
    showBottomBar = false,     // Detay sayfalarında genelde BottomBar gizlenir
    navController = navController,
    topAppBarActions = {
        IconButton(onClick = { /* Paylaş */ }) {
            Icon(Icons.Default.Share, contentDescription = "Paylaş")
        }
    }
) { paddingValues ->
    // Detay sayfası içeriği
}
```

#### 3. Ayarlar Sayfası
```kotlin
StandardLayout(
    onNavigateBack = { navController.popBackStack() },
    topAppBarTitle = stringResource(R.string.settings_title),
    showTopBar = true,
    showBackButton = true,
    showBottomBar = true,      // Ayarlar sayfasında BottomBar gösterilebilir
    navController = navController
) { paddingValues ->
    // Ayarlar içeriği
}
```

#### 4. Profil Sayfası
```kotlin
StandardLayout(
    navController = navController,
    title = stringResource(R.string.profile_title),
    showTopBar = true,
    showBackButton = false,    // Profil ana navigasyon öğesi ise geri butonu olmaz
    showBottomBar = true,
    onNavigateBack = { navController.popBackStack() },
    topAppBarActions = {
        IconButton(onClick = { /* Düzenle */ }) {
            Icon(Icons.Default.Edit, contentDescription = "Düzenle")
        }
    }
) { paddingValues ->
    // Profil içeriği
}
```

### Scroll Behavior
TopBar'ın kaydırma davranışını özelleştir:

```kotlin
// Scroll sırasında TopBar kaybolur
scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

// Scroll sırasında TopBar sabit kalır
scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

// TopBar scroll ile birlikte hareket eder
scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
```

Scroll behavior kullanırken içeriği `nestedScroll` modifier ile bağla:

```kotlin
val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

StandardLayout(
    // ... diğer parametreler
    scrollBehavior = scrollBehavior
) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        // Scroll edilebilir içerik
    }
}
```

## 🔄 Asenkron İşlemler

### Coroutines
- Asenkron işlemler için Kotlin Coroutines kullan
- ViewModel içinde `viewModelScope` kullan
- Composable içinde `rememberCoroutineScope()` kullan
- Flow'lar için `collectAsState()` kullan

```kotlin
// ViewModel içinde
fun loadData() {
    viewModelScope. launch {
        try {
            val result = repository.fetchData()
            _uiState.value = UiState.Success(result)
        } catch (e: Exception) {
            _uiState. value = UiState.Error(e. message)
        }
    }
}
```

## 🗺️ Navigation

### Navigation Compose
- Her ekran için `Screen` sealed class'ında route tanımla
- Navigation graph'ları modüler olarak ayır (auth, home, profile)
- Deep link desteği için route'lara parametre ekle
- Back stack yönetimi için `popUpTo` kullan

```kotlin
// İyi ✅
navController.navigate(Screen.Profile.route) {
    popUpTo(Screen. Home.route) { inclusive = false }
    launchSingleTop = true
}
```

### Uygulamadan Çıkış Kontrolü (HandleAppExit)
**ZORUNLU:** Uygulamadan çıkış kontrolü için `HandleAppExit` utility fonksiyonunu kullan

- Çıkış kontrolünü **MainActivity** seviyesinde merkezi olarak yönet
- **Sadece ana sayfada (Home Screen)** çıkış dialog'u göster
- Tek geri tuşu ile dialog açılır (double-tap gerektirmez)
- Diğer sayfalarda normal back navigation devam eder

```kotlin
// MainActivity.kt içinde - İyi ✅
@Composable
fun IzsuNavigation() {
    val navController = rememberNavController()
    
    // Mevcut route'u izle
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // Sadece Home ekranında çıkış dialog'u göster
    if (currentRoute == Screen.Home.route) {
        HandleAppExit(
            exitTitle = stringResource(id = R.string.exit_app_title),
            exitMessage = stringResource(id = R.string.exit_app_message),
            yesText = stringResource(id = R.string.yes),
            noText = stringResource(id = R.string.no)
        )
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Navigation graph
    }
}

// Kötü ❌ - Her ekranda BackHandler kullanmak
@Composable
fun HomeScreen(navController: NavController) {
    var showExitDialog by remember { mutableStateOf(false) }
    BackHandler { showExitDialog = true } // Kod tekrarı
    // ...
}
```

**HandleAppExit Özellikleri:**
- ✅ Tek yerden yönetim (MainActivity)
- ✅ Kod tekrarını önler
- ✅ Sadece belirtilen route'ta çalışır
- ✅ Otomatik Activity.finish() çağrısı
- ✅ Material Design 3 AlertDialog kullanır

## 🔥 Firebase Entegrasyonu

### Authentication
- Firebase Authentication için `AuthRepository` kullan
- Google Sign-In için Google Identity Services (GIS) kullan
- Tüm auth işlemlerini Flow olarak döndür
- Hata yönetimi için `try-catch` blokları kullan

```kotlin
// Repository pattern
suspend fun signInWithEmail(email: String, password: String): Flow<AuthResult> = flow {
    try {
        val result = auth.signInWithEmailAndPassword(email, password). await()
        emit(AuthResult.Success(result. user?. toUser()))
    } catch (e: FirebaseAuthException) {
        emit(AuthResult.Error(e. toAppException()))
    }
}
```

## 📱 UI Bileşenleri

### Özel Bileşenler
- Yeniden kullanılabilir bileşenler `ui/components` klasöründe olmalı
- Standart layout bileşenleri `ui/common` klasöründe olmalı
- Her bileşen için varsayılan parametreler tanımla
- Bileşenler tek sorumluluk prensibi ile tasarlanmalı
- Özel bileşen isimleri projeyi yansıtmalı (örn: `IzsuLogo`)

### Standart Bileşenler
Projede kullanılması gereken standart bileşenler:
- **StandardLayout** - Tüm ekranlar için temel layout
- **StandartTopAppBar** - Üst navigasyon çubuğu
- **StandardBottomBar** - Alt navigasyon çubuğu
- **HandleAppExit** - Uygulamadan çıkış kontrolü (MainActivity'de kullanılır)
- **ListMenuItem** - Liste öğeleri için
- **IzsuLogo** - Uygulama logosu

### Liste Öğeleri
- Liste öğeleri için `ListMenuItem` bileşenini kullan
- Tutarlılık için `ListMenuItemStyle` enum'ını kullan
- Leading/trailing content için lambda parametreleri sağla

## 🌍 Çoklu Dil Desteği

### Lokalizasyon
- Tüm metinler `strings.xml` dosyasında tanımlanmalı
- Composable içinde `stringResource()` kullan
- `LocaleHelper` utility'si ile dil değişimlerini yönet
- Hard-coded string kullanma

```kotlin
// İyi ✅
Text(text = stringResource(id = R.string.welcome_message))

// Kötü ❌
Text(text = "Hoş geldiniz")
```

## 🎨 Tema Yönetimi

### Theme Configuration
- `ThemeViewModel` ile tema durumunu yönet
- Tema tercihleri DataStore ile saklanmalı
- Dark/Light mode desteği sağla
- Dinamik renk desteği ekle (Material You)

```kotlin
IzsuAppTheme(themeConfig = themeConfig) {
    // Uygulama içeriği
}
```
## 📐 Kod Düzeni ve Format

### Dosya Yapısı
1. Package declaration
2. Import statements (alfabetik sıralı)
3. Class/Object declaration
4. Companion object (varsa)
5. Properties
6. Init block (varsa)
7. Functions

### Fonksiyon Sıralaması
1. Public functions
2. Private functions
3.  Composable functions (alfabetik)
4. Preview functions

### Yorum ve Dokümantasyon
- Public API'ler için KDoc açıklamaları yaz
- Karmaşık iş mantığı için açıklayıcı yorumlar ekle
- TODO yorumları için task referansı ekle
- Türkçe veya İngilizce tutarlı kullan

```kotlin
/**
 * Kullanıcı kimlik doğrulama işlemlerini yöneten ViewModel. 
 *
 * @property authRepository Kimlik doğrulama repository'si
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    // İçerik
}
```

## 🔒 Güvenlik

### Hassas Bilgiler
- API key'leri `local.properties` veya `BuildConfig` ile yönet
- Hassas bilgileri loglama
- ProGuard/R8 kurallarını tanımla
- Firebase Security Rules'ı uygula

## ⚡ Performans

### Optimization
- LazyColumn/LazyRow kullanırken `key` parametresi belirle
- Gereksiz recomposition'ları önle
- `derivedStateOf` ile hesaplanan state'leri optimize et
- Büyük listeler için pagination uygula

```kotlin
// İyi ✅
LazyColumn {
    items(
        items = userList,
        key = { user -> user.uid }
    ) { user ->
        UserItem(user = user)
    }
}
```

## 📦 Dependency Injection

### Manuel DI
- Constructor injection tercih et
- Repository instance'larını tekrar kullan
- ViewModel factory pattern'i kullan
- Gelecekte Hilt/Koin geçişine hazır kod yaz

### Repository Interface Pattern
**ZORUNLU:** Tüm Repository sınıfları için Interface kullan
- Her Repository için bir Interface tanımla (`IAuthRepository`, `IUserPreferencesRepository`)
- Concrete implementasyon Interface'i implement etmeli
- ViewModel'ler Interface'e bağımlı olmalı, concrete class'a değil
- Preview ve test için Fake Repository implementasyonları oluştur

```kotlin
// İyi ✅ - Interface kullanımı
interface IAuthRepository {
    suspend fun signInWithEmail(email: String, password: String): AuthResult<User>
    fun getCurrentUser(): User?
    fun signOut()
}

class AuthRepository : IAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    override suspend fun signInWithEmail(email: String, password: String): AuthResult<User> {
        // Firebase implementasyonu
    }
    
    override fun getCurrentUser(): User? {
        // Firebase implementasyonu
    }
    
    override fun signOut() {
        auth.signOut()
    }
}

// ViewModel Interface'e bağımlı
class AuthViewModel(
    private val repository: IAuthRepository = AuthRepository()
) : ViewModel() {
    // ViewModel içeriği
}

// Kötü ❌ - Doğrudan concrete class kullanımı
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    // Bu yaklaşım test ve preview için esneklik sağlamaz
}
```

### Fake Repository Pattern
Preview ve test için `data/repository/fake/` klasöründe Fake Repository'ler oluştur

```kotlin
// data/repository/fake/FakeAuthRepository.kt
class FakeAuthRepository : IAuthRepository {
    private var currentUser: User? = MOCK_USER
    
    override suspend fun signInWithEmail(email: String, password: String): AuthResult<User> {
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            currentUser = MOCK_USER.copy(email = email)
            AuthResult.Success(currentUser!!)
        } else {
            AuthResult.Error(AppException.AuthError("ERROR_INVALID_CREDENTIALS"))
        }
    }
    
    override fun getCurrentUser(): User? = currentUser
    
    override fun signOut() {
        currentUser = null
    }
    
    companion object {
        val MOCK_USER = User(
            uid = "preview_user_123",
            fullName = "Önizleme Kullanıcı",
            email = "preview@izsu.com",
            photoUrl = null
        )
    }
}
```

### Preview'larda Fake Repository Kullanımı
**ZORUNLU:** Tüm Preview fonksiyonlarında Fake Repository kullan

```kotlin
// İyi ✅ - Fake Repository ile Preview
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    IzsuAppTheme {
        LoginScreen(
            navController = rememberNavController(),
            viewModel = AuthViewModel(
                repository = FakeAuthRepository()
            ),
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}

// Kötü ❌ - Gerçek Repository ile Preview (Firebase hatası verir)
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    IzsuAppTheme {
        LoginScreen(
            navController = rememberNavController(),
            viewModel = AuthViewModel(), // Firebase bağımlılığı hata verir
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}
```

### Repository Interface Checklist
✅ Her Repository için Interface tanımla  
✅ Tüm public metodları Interface'e ekle  
✅ Concrete class Interface'i implement etsin  
✅ ViewModel constructor'da Interface tipini kullan  
✅ Fake Repository implementasyonu oluştur  
✅ Preview'larda Fake Repository kullan  
✅ Test'lerde Fake Repository kullan

## 🚫 Kaçınılması Gerekenler

❌ Hard-coded string'ler  
❌ Magic number'lar  
❌ God class'lar (çok fazla sorumluluk)  
❌ Tight coupling  
❌ Null pointer risk'i (`!! ` operatörü)  
❌ Memory leak'ler (lifecycle'ı dikkate alma)  
❌ Main thread'de ağır işlemler  
❌ Gereksiz nested Composable'lar  
❌ StandardLayout yerine manuel Scaffold kullanımı  
❌ Özel TopBar/BottomBar yerine standart bileşenleri kullanmamak  
❌ Her ekranda ayrı BackHandler ile çıkış kontrolü yapmak  
❌ HandleAppExit yerine manuel exit dialog implementasyonu  
❌ Repository'ler için Interface kullanmamak  
❌ Preview'larda gerçek Firebase Repository kullanımı  
❌ ViewModel'de concrete Repository class'ına doğrudan bağımlılık

## ✅ Best Practices

✅ Single Responsibility Principle  
✅ DRY (Don't Repeat Yourself)  
✅ KISS (Keep It Simple, Stupid)  
✅ Clean Code prensipleri  
✅ Meaningful naming  
✅ Consistent code style  
✅ Error handling  
✅ Null safety  
✅ StandardLayout kullanımı  
✅ Standart bileşenleri tercih etme  
✅ HandleAppExit ile merkezi çıkış kontrolü  
✅ Repository Interface Pattern kullanımı  
✅ Preview'larda Fake Repository kullanımı  
✅ Dependency Injection ile gevşek bağlantı

## 🔧 Proje Özellikleri

### Minimum Gereksinimler
- **minSdk:** 26 (Android 8.0)
- **targetSdk:** 35 (Android 15)
- **compileSdk:** 35
- **JVM Target:** 17
- **Kotlin Version:** Latest stable

### Kullanılan Kütüphaneler
- Jetpack Compose
- Material Design 3
- Firebase (Auth, Analytics, Crashlytics, Performance)
- Navigation Compose
- Kotlin Coroutines
- DataStore Preferences
- Google Play Services Auth

## 🎯 Kod Örnekleri

### ViewModel Pattern
```kotlin
// Basit ViewModel (Application parametresi almayan)
class AuthViewModel(
    private val repository: IAuthRepository = AuthRepository()
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = repository.signInWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.exception.message)
                }
            }
        }
    }
}

// AndroidViewModel (Application parametresi alır - Factory gerektirir)
class ThemeViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {
    
    val themeConfig: StateFlow<AppThemeConfig> = repository.themeConfigFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeConfig.FOLLOW_SYSTEM
        )
}

// Factory Pattern (AndroidViewModel için zorunlu)
class ThemeViewModelFactory(
    private val application: Application,
    private val repository: IUserPreferencesRepository? = null
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(
                application = application,
                repository = repository ?: UserPreferencesRepository.getInstance(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Kullanım
@Composable
fun MyScreen() {
    val context = LocalContext.current
    
    // Basit ViewModel
    val authViewModel: AuthViewModel = viewModel()
    
    // AndroidViewModel (Factory ile)
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModelFactory(context.applicationContext as Application)
    )
}
```

### Screen Composable Pattern (StandardLayout ile)
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    user: User?,
    onLogout: () -> Unit
) {
    StandardLayout(
        navController = navController,
        title = stringResource(R.string.profile_title),
        showTopBar = true,
        showBackButton = true,
        showBottomBar = true,
        onNavigateBack = { navController.popBackStack() },
        topAppBarActions = {
            IconButton(onClick = { /* Ayarlar */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Profil içeriği
            user?.let {
                Text(
                    text = it.fullName,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = it.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onLogout,
                modifier = Modifier. fillMaxWidth()
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    IzsuAppTheme {
        ProfileScreen(
            navController = rememberNavController(),
            user = User("1", "Test User", "test@example.com", null),
            onLogout = {}
        )
    }
}
```

---

**Not:** Bu kurallar projenin mevcut kod yapısına dayanarak oluşturulmuştur.  Copilot bu kuralları otomatik olarak tüm kod önerilerinde kullanacaktır.  **Özellikle yeni ekran oluştururken StandardLayout, StandartTopAppBar ve StandardBottomBar bileşenlerinin kullanılması zorunludur.** Uygulamadan çıkış kontrolü için **HandleAppExit utility fonksiyonunu MainActivity seviyesinde kullanın.**
