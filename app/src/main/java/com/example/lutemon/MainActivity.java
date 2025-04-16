package com.example.lutemon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Map;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 123;
    
    private Storage storage;
    private NavController navController;
    private DataManager dataManager;
    private boolean pendingExport = false;
    private boolean pendingImport = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize storage and data manager
        dataManager = new DataManager(this);
        storage = new Storage(this);

        // Set up navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                Log.d(TAG, "Navigating to: " + destination.getLabel());
            });
        } else {
            Log.e(TAG, "NavHostFragment not found!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_export) {
            checkPermissionAndExport();
            return true;
        } else if (id == R.id.action_import) {
            checkPermissionAndImport();
            return true;
        } else if (id == R.id.action_clear) {
            showClearConfirmation();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissionAndExport() {
        if (checkStoragePermission()) {
            exportLutemons();
        } else {
            pendingExport = true;
            requestStoragePermission();
        }
    }

    private void checkPermissionAndImport() {
        if (checkStoragePermission()) {
            importLutemons();
        } else {
            pendingImport = true;
            requestStoragePermission();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingExport) {
                    exportLutemons();
                } else if (pendingImport) {
                    importLutemons();
                }
            } else {
                showToast(R.string.message_permission_required);
            }
            pendingExport = false;
            pendingImport = false;
        }
    }

    private void exportLutemons() {
        if (dataManager.exportLutemons(storage.getAllLutemons())) {
            showToast(R.string.message_exported);
        } else {
            showToast(R.string.message_error);
        }
    }

    private void importLutemons() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.action_import)
            .setMessage(R.string.dialog_confirm_import)
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                Map<String, List<Lutemon>> importedLutemons = dataManager.importLutemons();
                if (importedLutemons != null) {
                    storage.setAllLutemons(importedLutemons);
                    storage.saveLutemons();
                    navController.navigate(R.id.nav_home);
                    showToast(R.string.message_imported);
                } else {
                    showToast(R.string.message_error);
                }
            })
            .setNegativeButton(android.R.string.no, null)
            .show();
    }

    private void showClearConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.action_clear)
            .setMessage(R.string.dialog_confirm_clear)
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                dataManager.clearData();
                storage = new Storage(this);
                navController.navigate(R.id.nav_home);
                showToast(R.string.message_cleared);
            })
            .setNegativeButton(android.R.string.no, null)
            .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    private void showToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        storage.saveLutemons();
        storage.saveStats();
        runOnUiThread(() -> showToast(R.string.message_saved));
    }

    /**
     * Gets the storage instance
     */
    public Storage getStorage() {
        return storage;
    }
}