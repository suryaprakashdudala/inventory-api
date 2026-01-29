package com.inventory.constants;

public class EmailTemplates {
	
    private EmailTemplates() {
        // Private constructor to prevent instantiation
    }

    // OTP EMAIL
    public static final String OTP_SUBJECT = "Password Reset OTP";

    public static final String OTP_BODY_TEMPLATE = """
        Hello,

        Your One-Time Password (OTP) for password reset is: %s

        This OTP is valid for the next 5 minutes.
        Please do not share this code with anyone for security reasons.

        Thank you,
        Inventory Managment
        """;


    // USER CREATION EMAIL
    public static final String USER_CREATION_SUBJECT = "User Creation";

    public static final String USER_CREATION_BODY_TEMPLATE = """
        Hello %s,

        Your account has been successfully created.

        Username: %s
        Temporary Password: %s

        Please log in and change your password immediately for security reasons.

        Thank you,
        Inventory Managment
        """;

    // DOCUMENT COMPLETION EMAIL
    public static final String DOCUMENT_COMPLETION_SUBJECT = "Action Required: Complete Document";

    public static final String DOCUMENT_COMPLETION_BODY_TEMPLATE = """
        Hello %s,

        You have been assigned to complete a document: %s

        Please click the link below to access and complete the document:
        %s

        This link is unique to you and will expire in 7 days.

        Thank you,
        Inventory Managment
        """;

    // FINAL DOCUMENT EMAIL
    public static final String FINAL_DOCUMENT_SUBJECT = "Document Completed: %s";

    public static final String FINAL_DOCUMENT_BODY_TEMPLATE = """
        Hello %s,

        The document '%s' has been signed by all parties.

        View final document:
        %s

        Thanks,
        Inventory Managment
        """;

    // LOW STOCK ALERT EMAIL
    public static final String LOW_STOCK_ALERT_SUBJECT = "Low Stock Alert: %s";

    public static final String LOW_STOCK_ALERT_BODY_TEMPLATE = """
        Hello %s,

        This is an automated alert to inform you that the product '%s' (SKU: %s) has fallen below its reorder level.

        Current Available Quantity: %d
        Reorder Level: %d

        Please take necessary actions for replenishment.

        Thank you,
        Inventory Management
        """;
}
