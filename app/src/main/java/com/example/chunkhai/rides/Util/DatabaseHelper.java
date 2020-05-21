package com.example.chunkhai.rides.Util;

public class DatabaseHelper {

    /**
     * Storage folder
     */
    public static final String FOLDER_PROFILE_PIC = "profilePic";
    public static final String FOLDER_VEHICLE_PIC = "vehiclePic";
    public static final String FOLDER_VERIFICATION = "verification";
    public static final String FOLDER_VERIFICATION_MATRIX = "matrix";
    public static final String FOLDER_VERIFICATION_LICENSE = "license";


    /**
     * Table User
     */
    public static final String TABLE_USER = "User";
    //Attributes table User
    public static final String TABLE_USER_ATTR_UID = "user_uid";
    public static final String TABLE_USER_ATTR_PROFILE_NAME = "user_profileName";
    public static final String TABLE_USER_ATTR_PASSWORD = "user_password";
    public static final String TABLE_USER_ATTR_EMAIL = "user_email";
    public static final String TABLE_USER_ATTR_PHONE_NO = "user_phoneNo";
    public static final String TABLE_USER_ATTR_GENDER = "user_gender";
    public static final String TABLE_USER_ATTR_SIGNATURE = "user_signature";
    public static final String TABLE_USER_ATTR_PROFILE_PIC_SRC = "user_profilePicSrc";
    public static final String TABLE_USER_ATTR_VERIF_LICENSE_STATUS = "user_verifLicenseStatus";
    public static final String TABLE_USER_ATTR_DEVICE_TOKEN = "user_deviceToken";
    public static final String TABLE_USER_ATTR_FULLNAME = "user_fullName";
    public static final String TABLE_USER_ATTR_MATRIX_NO= "user_matrixNo";
    public static final String TABLE_USER_ATTR_VERIF_MATRIX_SRC = "user_verifMatrixSrc";
    public static final String TABLE_USER_ATTR_LICENSE_NO = "user_licenseNo";
    public static final String TABLE_USER_ATTR_VERIF_LICENSE_SRC = "user_verifLicenseSrc";
    public static final String TABLE_USER_ATTR_VERIF_MATRIX_STATUS = "user_verifMatrixStatus";
    public static final String TABLE_USER_ATTR_REGISTER_TIMESTAMP = "user_registerTimestamp";
    public static final String TABLE_USER_ATTR_VERIF_MATRIX_TIMESTAMP = "user_verifMatrixTimestamp";
    public static final String TABLE_USER_ATTR_VERIF_LICENSE_TIMESTAMP = "user_verifLicenseTimestamp";

    /**
     * Table Vehicle
     */
    public static final String TABLE_VEHICLE = "Vehicle";
    //Attributes table Vehicle
    public static final String TABLE_VEHICLE_ATTR_UID= "vehicle_uid";
    public static final String TABLE_VEHICLE_ATTR_PLATE_NO= "vehicle_plateNo";
    public static final String TABLE_VEHICLE_ATTR_MODEL= "vehicle_model";
    public static final String TABLE_VEHICLE_ATTR_COLOR= "vehicle_color";
    public static final String TABLE_VEHICLE_ATTR_IMAGE_SRC= "vehicle_imageSrc";
    public static final String TABLE_VEHICLE_ATTR_CAPACITY= "vehicle_capacity";


    /**
     * Table ProvidedRide
     */
    public static final String TABLE_PROVIDEDRIDE = "ProvidedRide";
    //Attributes table ProvidedRide
    public static final String TABLE_PROVIDEDRIDE_ATTR_ID = "pr_id";
    public static final String TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID = "pr_providerUid";
    public static final String TABLE_PROVIDEDRIDE_ATTR_CREATED_TIMESTAMP = "pr_createdTimestamp";
    public static final String TABLE_PROVIDEDRIDE_ATTR_FROM_LATITUDE = "pr_fromLatitude";
    public static final String TABLE_PROVIDEDRIDE_ATTR_FROM_LONGITUDE = "pr_fromLongitude";
    public static final String TABLE_PROVIDEDRIDE_ATTR_FROM_PLACE = "pr_fromPlaceAddress";
    public static final String TABLE_PROVIDEDRIDE_ATTR_FROM_ADDRESS = "pr_fromAddress";
    public static final String TABLE_PROVIDEDRIDE_ATTR_TO_LATITUDE = "pr_toLatitude";
    public static final String TABLE_PROVIDEDRIDE_ATTR_TO_LONGITUDE = "pr_toLongitude";
    public static final String TABLE_PROVIDEDRIDE_ATTR_TO_PLACE = "pr_toPlace";
    public static final String TABLE_PROVIDEDRIDE_ATTR_TO_ADDRESS = "pr_toAddress";
    public static final String TABLE_PROVIDEDRIDE_ATTR_DEPART_TIMESTAMP = "pr_departTimestamp";
    public static final String TABLE_PROVIDEDRIDE_ATTR_VEHICLE_ID = "pr_vehicleId";
    public static final String TABLE_PROVIDEDRIDE_ATTR_FARE = "pr_fare";
    public static final String TABLE_PROVIDEDRIDE_ATTR_STATUS = "pr_status";

    /**
     * Table SharedRide
     */
    public static final String TABLE_SHAREDRIDE = "SharedRide";
    //Attributes table SharedRide
    public static final String TABLE_SHAREDRIDE_ATTR_ID = "rs_id";
    public static final String TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID = "rs_providedRideId";
    public static final String TABLE_SHAREDRIDE_ATTR_CARPOOLER_UID = "rs_carpoolerUid";
    public static final String TABLE_SHAREDRIDE_ATTR_NOTE = "rs_note";
    public static final String TABLE_SHAREDRIDE_ATTR_CREATED_TIMESTAMP = "rs_createdTimestamp";
    public static final String TABLE_SHAREDRIDE_ATTR_STATUS = "rs_status";

    /**
     * Table Notification
     */
    public static final String TABLE_NOTIFICATION = "Notification";
    //Attributes table SharedRide
    public static final String TABLE_NOTIFICATION_ATTR_ID = "notif_id";
    public static final String TABLE_NOTIFICATION_ATTR_RECEIVER_UID = "notif_reciever_uid";
    public static final String TABLE_NOTIFICATION_ATTR_MESSAGE = "notif_message";
    public static final String TABLE_NOTIFICATION_ATTR_STATUS = "notif_status";
    public static final String TABLE_NOTIFICATION_ATTR_TYPE = "notif_type";
    public static final String TABLE_NOTIFICATION_ATTR_CREATED_TIMESTAMP = "notif_createdTimestamp";
    public static final String TABLE_NOTIFICATION_ATTR_SENDER_UID = "notif_sender_uid";
    public static final String TABLE_NOTIFICATION_ATTR_REF_PROVIDED_RIDE_ID = "notif_ref_providedRideId";

    /**
     * Table RatingReview
     */
    public static final String TABLE_RATINGREVIEW= "RatingReview";
    //Attributes table SharedRide
    public static final String TABLE_RATINGREVIEW_ATTR_ID = "rr_id";
    public static final String TABLE_RATINGREVIEW_ATTR_RATER_UID = "rr_raterUid";
    public static final String TABLE_RATINGREVIEW_ATTR_RECEIVER_UID = "rr_receiverUid";
    public static final String TABLE_RATINGREVIEW_ATTR_RATING = "rr_rating";
    public static final String TABLE_RATINGREVIEW_ATTR_REVIEW = "rr_review";
    public static final String TABLE_RATINGREVIEW_ATTR_DATETIME_CREATED = "rr_createdTimeStamp";

    /**
     * Table EmergencyContact
     */
    public static final String TABLE_EMERGENCY_CONTACT= "EmergencyContact";

    /**
     * Table Discussion
     */
    public static final String TABLE_DISCUSSION= "Discussion";
    //Attributes table Discussion
    public static final String TABLE_DISCUSSION_ATTR_ID = "disc_id";
    public static final String TABLE_DISCUSSION_ATTR_CONTENT = "disc_content";
    public static final String TABLE_DISCUSSION_ATTR_TIMESTAMP = "disc_timestamp";
    public static final String TABLE_DISCUSSION_ATTR_SENDER = "disc_sender";
}
