package com.sobremesa.birdwatching.util;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sobremesa.birdwatching.BAMApplication;

public class AnalyticsUtil {

    public static final class Affiliations{
        public static final String REGULAR_ORDER = "Regular Order";

    }

    public static final class CurrencyCodes{
        public static final String USD = "USD";
        public static final String CND = "CND";
        public static final String EUR = "EUR";
    }

    public static final class Categories{
        public static final String GENERAL = "General";
        public static final String SIGHTINGS = "Sightings";
        public static final String BIRD_IMAGES = "Bird Images";
    }

    public static final class Actions{
        public static final String GENERAL = "General";
        public static final String SYNC = "Sync";
    }


    public static void sendView(String view){

        Tracker t = BAMApplication.getGATracker(
                BAMApplication.TrackerName.APP_TRACKER);
        t.setScreenName(view);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

//    public static void sendEvent(FragmentActivity activity, String view, String category, String action){
//
//        if( BAMConstants.PRODUCTION_MODE ) {
//            Tracker t = ((BAMApplication) activity.getApplication()).getGATracker(
//                    BAMApplication.TrackerName.APP_TRACKER);
//            t.setScreenName(view);
//            t.send(new HitBuilders.EventBuilder()
//                    .setCategory(category)
//                    .setAction(action)
//                    .build());
//        }
//    }


    public static void sendEvent(String view, String category, String action, String label){

        Tracker t = BAMApplication.getGATracker (
                BAMApplication.TrackerName.APP_TRACKER);
        t.setScreenName(view);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public static void sendTransaction(String transactionId, String affiliation, double revenue, double tax, double shipping, String currencyCode){
        Tracker tracker = BAMApplication.getGATracker (
                BAMApplication.TrackerName.APP_TRACKER);
        tracker.send(new HitBuilders.TransactionBuilder()
                .setTransactionId(transactionId)       // (String) Transaction ID, should be unique among transactions.
                .setAffiliation(affiliation)   // (String) Affiliation
                .setRevenue(revenue)      // (long) Order revenue (includes tax and shipping)
                .setTax(tax)      // (long) Tax
                .setShipping(shipping)              // (long) Shipping cost
                .setCurrencyCode(currencyCode)            // (String) Currency code
                .build());
    }

    public static void sendTransactionItem(String orderId, String itemName, String itemSku, String itemCategory, double price, long qty, String currencyCode){
        Tracker tracker = BAMApplication.getGATracker (
                BAMApplication.TrackerName.APP_TRACKER);
        tracker.send(new HitBuilders.ItemBuilder()
                .setTransactionId(orderId)
                .setName(itemName)
                .setSku(itemSku)
                .setCategory(itemCategory)
                .setPrice(price)
                .setQuantity(qty)
                .setCurrencyCode(currencyCode)
                .build());
    }

}
