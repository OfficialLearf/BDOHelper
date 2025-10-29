package com.example.bdoapp.UI

object AppStyles {
    fun getStylesheet(): String {
        return """
            .root {
                -fx-background-color: #1a1a1a;
                -fx-font-family: 'Segoe UI', Arial, sans-serif;
            }
            
            .main-container {
                -fx-background-color: #1a1a1a;
            }
            
            .title-label {
                -fx-font-size: 32px;
                -fx-font-weight: bold;
                -fx-text-fill: #e74c3c;
            }
            
            .subtitle-label {
                -fx-font-size: 16px;
                -fx-text-fill: #95a5a6;
            }
            
            .section-title {
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-text-fill: #ecf0f1;
            }
            
            .label-text {
                -fx-text-fill: #bdc3c7;
                -fx-font-size: 14px;
            }
            
            .menu-button {
                -fx-background-color: #2c2c2c;
                -fx-text-fill: #ecf0f1;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-background-radius: 8px;
                -fx-border-color: #3a3a3a;
                -fx-border-radius: 8px;
                -fx-border-width: 1px;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);
            }
            
            .menu-button:hover {
                -fx-background-color: #333333;
                -fx-border-color: #e74c3c;
            }
            
            .menu-button:pressed {
                -fx-background-color: #3a3a3a;
            }
            
            .btn-primary {
                -fx-background-color: #e74c3c;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6px;
                -fx-padding: 10px 20px;
                -fx-cursor: hand;
            }
            
            .btn-primary:hover {
                -fx-background-color: #c0392b;
            }
            
            .btn-secondary {
                -fx-background-color: #34495e;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6px;
                -fx-padding: 10px 20px;
                -fx-cursor: hand;
            }
            
            .btn-secondary:hover {
                -fx-background-color: #2c3e50;
            }
            
            .btn-danger {
                -fx-background-color: transparent;
                -fx-text-fill: #e74c3c;
                -fx-font-weight: bold;
                -fx-background-radius: 6px;
                -fx-padding: 8px 20px;
                -fx-border-color: #e74c3c;
                -fx-border-radius: 6px;
                -fx-border-width: 2px;
                -fx-cursor: hand;
            }
            
            .btn-danger:hover {
                -fx-background-color: #e74c3c;
                -fx-text-fill: white;
            }
            
            .text-field {
                -fx-background-color: #2c2c2c;
                -fx-text-fill: #ecf0f1;
                -fx-border-color: #3a3a3a;
                -fx-border-radius: 6px;
                -fx-background-radius: 6px;
                -fx-padding: 10px;
                -fx-font-size: 14px;
            }
            
            .text-field:focused {
                -fx-border-color: #e74c3c;
                -fx-border-width: 2px;
            }
            
            .text-area {
                -fx-background-color: #2c2c2c;
                -fx-text-fill: #ecf0f1;
                -fx-border-color: #3a3a3a;
                -fx-border-radius: 6px;
                -fx-background-radius: 6px;
                -fx-padding: 10px;
                -fx-font-size: 13px;
                -fx-font-family: 'Consolas', 'Courier New', monospace;
            }
            
            .text-area:focused {
                -fx-border-color: #e74c3c;
                -fx-border-width: 2px;
            }
            
            .text-area .content {
                -fx-background-color: #2c2c2c;
            }
            
            .list-view {
                -fx-background-color: #2c2c2c;
                -fx-border-color: #3a3a3a;
                -fx-border-radius: 6px;
                -fx-background-radius: 6px;
                -fx-border-width: 1px;
            }
            
            .list-view .list-cell {
                -fx-background-color: transparent;
                -fx-text-fill: #ecf0f1;
                -fx-padding: 10px;
                -fx-font-size: 14px;
            }
            
            .list-view .list-cell:selected {
                -fx-background-color: #e74c3c;
                -fx-text-fill: white;
            }
            
            .list-view .list-cell:hover {
                -fx-background-color: #333333;
            }
            
            .list-view:focused .list-view .list-cell:selected {
                -fx-background-color: #e74c3c;
            }
            
            .scroll-bar {
                -fx-background-color: transparent;
            }
            
            .scroll-bar .thumb {
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 4px;
            }
            
            .scroll-bar .thumb:hover {
                -fx-background-color: #4a4a4a;
            }
            
            .scroll-bar .track {
                -fx-background-color: #2c2c2c;
            }
            .card:hover {
                -fx-background-color: #333333;
                -fx-border-color: #e74c3c; /* Your red outline color */
                -fx-border-width: 2px;
            }
            .card {
                -fx-background-color: #2c2c2c;
                -fx-background-radius: 8px;
                -fx-border-color: #3a3a3a;
                -fx-border-radius: 8px;
                -fx-border-width: 1px;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);
            }
        """.trimIndent()
    }
}