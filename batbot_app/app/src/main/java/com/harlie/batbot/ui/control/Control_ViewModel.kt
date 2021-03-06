// Copyright (c) 2019, Lee Hounshell. All rights reserved.

package com.harlie.batbot.ui.control

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.batbot.model.RobotCommandModel
import com.harlie.batbot.service.BluetoothChatService
import com.harlie.batbot.event.BluetoothCaptureImageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class Control_ViewModel : ViewModel() {
    val TAG = "LEE: <" + Control_ViewModel::class.java.getName() + ">"

    private var m_initialized = false
    private var m_BluetoothChatService = BluetoothChatService()

    private lateinit var m_inputCommand: MutableLiveData<RobotCommandModel>
    private lateinit var m_textOutputClicked: MutableLiveData<Boolean>
    private lateinit var m_starClicked: MutableLiveData<Boolean>
    private lateinit var m_okClicked: MutableLiveData<Boolean>
    private lateinit var m_sharpClicked: MutableLiveData<Boolean>
    private lateinit var m_captureImageEvent: MutableLiveData<BluetoothCaptureImageEvent>
    private lateinit var m_bluetoothAdapter: BluetoothAdapter


    fun initLiveData() {
        Log.d(TAG, "initLiveData")
        // if bluetooth fails these need to be reset for the next activity
        m_inputCommand = MutableLiveData<RobotCommandModel>()
        m_textOutputClicked = MutableLiveData<Boolean>()
        m_starClicked = MutableLiveData<Boolean>()
        m_okClicked = MutableLiveData<Boolean>()
        m_sharpClicked = MutableLiveData<Boolean>()
        m_captureImageEvent = MutableLiveData<BluetoothCaptureImageEvent>()
    }

    fun getInputCommand(): LiveData<RobotCommandModel> = m_inputCommand
    fun getTextOutputClicked(): LiveData<Boolean> = m_textOutputClicked
    fun getStarClicked(): LiveData<Boolean> = m_starClicked
    fun getOkClicked(): LiveData<Boolean> = m_okClicked
    fun getSharpClicked(): LiveData<Boolean> = m_sharpClicked
    fun getCaptureImage(): LiveData<BluetoothCaptureImageEvent> = m_captureImageEvent

    fun initialize(): BluetoothAdapter {
        if (! m_initialized) {
            Log.d(TAG, "initialize")
            EventBus.getDefault().register(this)
            m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            m_initialized = true
        }
        return m_bluetoothAdapter
    }

    fun processAndDecodeMessage(robotCommand: RobotCommandModel) {
        Log.d(TAG, "processAndDecodeMessage: " + robotCommand.robotCommand + ", priority=" + robotCommand.commandPriority)
        // FUTURE: pre-analyze the command using ANTLR?
        m_inputCommand.postValue(robotCommand)
    }

    fun doClickTextOutput() {
        Log.d(TAG, "doClickTextOutput")
        m_textOutputClicked.postValue(true)
    }

    fun doClickStar() {
        Log.d(TAG, "doClickStar")
        m_starClicked.postValue(true)
    }

    fun doClickOk() {
        Log.d(TAG, "doClickOk")
        m_okClicked.postValue(true)
    }

    fun doClickSharp() {
        Log.d(TAG, "doClickSharp")
        m_sharpClicked.postValue(true)
    }

    fun connect(device: BluetoothDevice, b: Boolean) {
        Log.d(TAG, "connect: " + device.name)
        m_BluetoothChatService.connect(device, true)
    }

    fun send(message: String) {
        //Log.d(TAG, "send: '" + message + "'")
        m_BluetoothChatService.send(message)
    }

    fun disconnect() {
        Log.d(TAG, "disconnect")
        m_BluetoothChatService.stop()
    }

    fun uploadImage(imageFile: String, imageSize: Int) {
        Log.d(TAG, "uploadImage")
        m_BluetoothChatService.uploadImage(imageFile, imageSize)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBluetoothMessageEvent(bt_image_event: BluetoothCaptureImageEvent) {
        Log.d(TAG, "onBluetoothMessageEvent")
        m_BluetoothChatService.setCapturingImage(false)
        m_captureImageEvent.setValue(bt_image_event)
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared")
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }
}
