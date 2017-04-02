package com.gaj2l.eventtus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gaj2l.eventtus.R;
import com.gaj2l.eventtus.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

/**
 * Created by Lucas Tomasi on 28/03/17.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private static final int RC_SIGN_IN = 9001;
    private SignInButton    signInButton;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private LoginButton     btnFacebook;
    private Button          btnFace;
    private Button          btnGoogle;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Login Google
        mGoogleApiClient = new GoogleApiClient.Builder(this).addOnConnectionFailedListener(this).addApi( Auth.GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build() ).build();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        //Login Facebook
        btnFacebook     = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        btnFacebook.registerCallback( callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture");

                GraphRequest request = GraphRequest.newMeRequest( loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try
                        {
                            User user = new User();
                            user.setName(object.getString("name"));
                            user.setMail(object.getString("email"));
                            user.setMethodAutentication(User.METHOD_FACEBOOK);
                            if( object.has("picture"))
                                user.setImage(object.getJSONObject("picture").getJSONObject("data").getString("url") );

                            redirect(user);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                Toast.makeText( LoginActivity.this,R.string.err_btn_facebook,Toast.LENGTH_LONG).show();
            }
        });
        //

        // Botões
        btnFacebook.setVisibility(View.INVISIBLE);
        btnFace = (Button) findViewById(R.id.btnFace);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                btnFacebook.callOnClick();
            }
        });

        signInButton.setVisibility(View.INVISIBLE);
        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                signIn();
            }
        });
        //
    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        if (result.isSuccess())
        {
            GoogleSignInAccount acct = result.getSignInAccount();

            User user = new User();
            user.setName(acct.getDisplayName());
            user.setMail(acct.getEmail());
            user.setMethodAutentication(User.METHOD_GOOGLE);
            if(acct.getPhotoUrl()!=null)
                user.setImage(acct.getPhotoUrl().toString());

            redirect(user);
        }
    }

    private void redirect( User user )
    {
        Intent event = new Intent(LoginActivity.this, EventActivity.class);
        event.putExtra( "username" , user.getName() );
        event.putExtra( "email"    , user.getMail() );

        event.putExtra( "image"    , user.getImage() );
        startActivity( event );
    }

    public void logout()
    {
        // Logout Google
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {}
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {}
        });

        // Logout Facebook
        if( Profile.getCurrentProfile() != null  )
        {
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Login Google
        if (requestCode == RC_SIGN_IN)
        {
            handleSignInResult( Auth.GoogleSignInApi.getSignInResultFromIntent(data) );
        }

        // Login Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText( LoginActivity.this,R.string.err_btn_google,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}