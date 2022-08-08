package com.zybooks.babyonboard

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_bluetooth_page.*
import kotlinx.android.synthetic.main.fragment_bluetooth_page.view.*


class BluetoothPageFragment : Fragment() {

    private val REQUEST_CODE_ENABLE_BT:Int = 1
    private val REQUEST_CODE_DISCOVERABLE_BT:Int = 2


    //notification variables
    /*
    private var _binding: FragmentBluetoothPageBinding? = null
    private val binding get() = _binding!!
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIF_ID = 0

     */

    val CHANNEL_ID = "channelID"
    var notificationId = 0

    //bluetooth adapter
    private lateinit var bAdapter: BluetoothAdapter

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_bluetooth_page, container, false)

        //_binding = FragmentBluetoothPageBinding.inflate(inflater, container, false)

        /*
        createNotifChannel()
        val intent=Intent(this,BluetoothPageFragment::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        NotificationCompat.Builder(Context context, String channelId)

        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Sample Title")
            .setContentText("This is sample body notif")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notifManger = NotificationManagerCompat.from(this)


        binding.turnOnBtn.setOnClickListener {
            notifManger.notify(NOTIF_ID,notif)
        }

         */

        //define your channel id
        val CHANNEL_ID = "your_channel_id"

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL_ID , "Your channel name", NotificationManager.IMPORTANCE_DEFAULT)
            NotificationManagerCompat.from(requireContext()).createNotificationChannel(channel)
        }

        //val bitmap = BitmapFactory.decodeResource(applicationContext.resouces, R.drawable.ic_bob_2_xxxhdpi)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_bob_2_xxxhdpi)

        //val bpStyle = NotificationCompat.BigPictureStyle()
        //bpStyle.bigPicture(BitmapFactory.decodeResource(resources, R.drawable.ic_bob_xxxhdpi)).build()

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(requireContext(), ResultActivity::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(requireContext()).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }


        var builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(bitmap)
            //.setStyle(bpStyle)
            .setContentTitle("BLUETOOTH CONNECTED")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Please See That The Baby On Board Is Secured Properly. Reminder: BOB Is Not Responsible For Technical Malfunctions With Bluetooth Capability "))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)




        /*
        with(NotificationManagerCompat.from(requireContext())) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }

        createNotificationChannel()

         */



        //init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()

        //check if bluetooth is on/off
        if(bAdapter ==null){
            view.bluetoothStatusTv.text = "Bluetooth is not available"

        } else {
            view.bluetoothStatusTv.text = "Bluetooth is available"
        }


        //set image according to bluetooth status(on/off)
        if(bAdapter.isEnabled){
            //bluetooth is on
            view.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
        } else {
            //bluetooth is off
            view.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
        }


        //turn on bluetooth
        view.turnOnBtn.setOnClickListener {
            if (bAdapter.isEnabled){
                //already enabled
                Toast.makeText(context, "already on", Toast.LENGTH_LONG).show()
                NotificationManagerCompat.from(requireContext()).notify(notificationId, builder.build())

            } else {
                //tun on bluetooth
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }

        //turn off bluetooth
        view.turnOffBtn.setOnClickListener {
            if (!bAdapter.isEnabled){
                //already enabled
                Toast.makeText(context, "already off", Toast.LENGTH_LONG).show()
            } else {
                //tun on bluetooth
                bAdapter.disable()
                bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
                Toast.makeText(context, "Bluetooth turned off", Toast.LENGTH_LONG).show()

            }
        }

        //discoverable the bluetooth
        view.discoverableBtn.setOnClickListener {
            if (!bAdapter.isDiscovering){
                Toast.makeText(context, "Making your device discoverable", Toast.LENGTH_LONG).show()
                val intent = (Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT )
            }
        }
        //get list of paired devices
        view.pairedBtn.setOnClickListener{
            if(bAdapter.isEnabled){
                pairedTv.text = "Paired Devices"
                //get list of paired devices
                val devices = bAdapter.bondedDevices
                for (device in devices){
                    val deviceName = device.name
                    val deviceAddress = device
                    pairedTv.append("\nDevice: $deviceName, $device")
                }
            } else {
                Toast.makeText(context, "Turn on bluetooth first", Toast.LENGTH_LONG).show()

            }
        }


        // This callback will only be called when the fragment is at least started.
        // Back button functionality
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val methodSelectionFrag = MethodSelectionFragment()
                    val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.nav_host_fragment, methodSelectionFrag)
                        .commit()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        // The callback can be enabled or disabled here or in handleOnBackPressed()

        return view
        //return binding.root

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_CODE_ENABLE_BT ->
                if(resultCode== Activity.RESULT_OK){
                    bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(context, "Bluetooth is on", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(context, "Could not turn on bluetooth", Toast.LENGTH_LONG).show()

                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    /*
    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

     */

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}





