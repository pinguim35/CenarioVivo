package br.com.cenariovivo.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build // Adicione esta importação
import android.os.Bundle
import android.util.Log // <--- ADICIONE ESTA LINHA
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout // Importe ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat // Adicione esta importação
import androidx.core.view.updatePadding // Importe esta função de extensão

class MainActivity : AppCompatActivity() {
    // ... dentro da classe MainActivity
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted")
            } else {
                Log.d("MainActivity", "Notification permission denied")
                // Opcional: Informe ao usuário que ele não receberá notificações
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU é API 33
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permissão já concedida
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Opcional: Mostrar uma UI explicando por que você precisa da permissão
                // e então chamar requestPermissionLauncher.launch(...)
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) // Lança mesmo assim por agora
            } else {
                // Solicitar a permissão diretamente
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        // 1. Habilitar Edge-to-Edge ANTES de setContentView
        // Isso permite que o conteúdo da sua activity seja desenhado sob as barras do sistema.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        // --- INÍCIO DA MODIFICAÇÃO PARA ÍCONES ESCUROS NA STATUS BAR ---
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // Define que os ícones da status bar devem ser escuros
        windowInsetsController.isAppearanceLightStatusBars = true
        // --- FIM DA MODIFICAÇÃO ---

        // Com o tema AppCompat e edge-to-edge, esconder a action bar pode ser redundante
        // se o seu tema já for NoActionBar. Mas não prejudica.
        supportActionBar?.hide()

        // 2. Obter a view raiz (o ConstraintLayout) do seu activity_main.xml
        // Você precisa aplicar os insets à view que contém o WebView.
        // No seu caso, é o ConstraintLayout pai do WebView.
        // Como o ID do WebView é 'webview', e ele é filho direto do ConstraintLayout,
        // podemos pegar o pai do WebView.
        val webViewContainer = findViewById<WebView>(R.id.webview).parent as ConstraintLayout
        // Alternativamente, se o ConstraintLayout tivesse um ID, você poderia usar:
        // val webViewContainer = findViewById<ConstraintLayout>(R.id.seu_id_do_constraintlayout)


        // 3. Aplicar os insets como padding à view container do WebView
        ViewCompat.setOnApplyWindowInsetsListener(webViewContainer) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Aplica padding para que o conteúdo dentro do webViewContainer
            // (que é o seu WebView) não fique sob as barras do sistema.
            view.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right,
                bottom = insets.bottom
            )
            // Retorna os insets consumidos para que outras views não tentem aplicá-los novamente.
            WindowInsetsCompat.CONSUMED
        }

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.webViewClient = WebViewClient()
        myWebView.settings.javaScriptEnabled = true
        myWebView.loadUrl("https://www.cenariovivo.com.br")
    }
}
