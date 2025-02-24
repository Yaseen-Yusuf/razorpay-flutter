package com.razorpay.razorpay_flutter;

import androidx.annotation.NonNull;
import java.util.Map;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * RazorpayFlutterPlugin
 */
public class RazorpayFlutterPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

    private RazorpayDelegate razorpayDelegate;
    private ActivityPluginBinding pluginBinding;
    private static final String CHANNEL_NAME = "razorpay_flutter";
    private Map<String, Object> _arguments;
    private String customerMobile;
    private String color;

    public RazorpayFlutterPlugin() {
    }

    // Define an enum for the method names.
    private enum MethodName {
        open,
        setPackageName,
        resync,
        setKeyID,
        linkNewUpiAccount,
        manageUpiAccounts,
        isTurboPluginAvailable
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        // Clean up if necessary.
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMethodCall(MethodCall call, Result result) {
        // Convert call.method string to enum.
        MethodName method;
        try {
            method = MethodName.valueOf(call.method);
        } catch (IllegalArgumentException e) {
            result.notImplemented();
            return;
        }

        switch (method) {
            case open:
                razorpayDelegate.openCheckout((Map<String, Object>) call.arguments, result);
                break;
            case setPackageName:
                razorpayDelegate.setPackageName((String) call.arguments);
                break;
            case resync:
                razorpayDelegate.resync(result);
                break;
            case setKeyID:
                String key = call.arguments().toString();
                razorpayDelegate.setKeyID(key, result);
                break;
            case linkNewUpiAccount:
                _arguments = call.arguments();
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.linkNewUpiAccount(customerMobile, color, result);
                break;
            case manageUpiAccounts:
                _arguments = call.arguments();
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.manageUpiAccounts(customerMobile, color, result);
                break;
            case isTurboPluginAvailable:
                razorpayDelegate.isTurboPluginAvailable(result);
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.razorpayDelegate = new RazorpayDelegate(binding.getActivity());
        this.pluginBinding = binding;
        binding.addActivityResultListener(razorpayDelegate);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        if (pluginBinding != null && razorpayDelegate != null) {
            pluginBinding.removeActivityResultListener(razorpayDelegate);
        }
        pluginBinding = null;
    }
}
