import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*
import io.ktor.common.client.*
import io.ktor.common.client.http.*

fun main(args: Array<String>) {
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("konan") + args).map { it.cstr.ptr }.toCValues()

        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(AppDelegate))
        }
    }
}

class AppDelegate @OverrideInit constructor() : UIResponder(), UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta {}

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }
}


@ExportObjCClass
class ViewController : UIViewController {

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @ObjCOutlet
    lateinit var label: UILabel

    @ObjCOutlet
    lateinit var textField: UITextField

    @ObjCOutlet
    lateinit var button: UIButton

    @ObjCAction
    fun buttonPressed() {
        performRequest(textField.text!!)
    }

    fun performRequest(endpoint: String) {
        val HEADER = "---==="
        val client = HttpClient()

        promise {
            client.request {
                with(url) {
                    protocol = URLProtocol.HTTPS
                    host = endpoint
                    port = 443
                }
            }
        }.then { response ->
            println("$HEADER request: ${response.request.url}")
            println("$HEADER response status: ${response.statusCode}")
            println("$HEADER headers:")
            response.headers.forEach { (key, values) ->
                println("  -$key: ${values.joinToString()}")
            }
            println("$HEADER body:")
            println(response.body)
            label.text = response.body

            client.close()
        }
    }
}


