package com.zybooks.babyonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction

class HomeScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)
        val button = view.findViewById<Button>(R.id.yesBtn)
        button.setOnClickListener(yesBtnListener)

        return view
    }

    private val yesBtnListener = View.OnClickListener {
        val methodSelectionFrag = MethodSelectionFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.nav_host_fragment, methodSelectionFrag)
            .commit()
    }
}