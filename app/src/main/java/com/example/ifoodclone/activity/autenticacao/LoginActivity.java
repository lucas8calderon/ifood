package com.example.ifoodclone.activity.autenticacao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.empresa.EmpresaHomeActivity;
import com.example.ifoodclone.activity.empresa.EmpresaFinalizaCadastroActivity;
import com.example.ifoodclone.activity.usuario.UsuarioFinalizaCadastroActivity;
import com.example.ifoodclone.activity.usuario.UsuarioHomeActivity;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Empresa;
import com.example.ifoodclone.model.Login;
import com.example.ifoodclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText edt_email;
    private EditText edt_senha;
    private ProgressBar progressBar;

    private Login login;

    private ImageButton facebook, google, twitter;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        configCliques();

        iniciaComponentes();

    }

    private void configCliques() {
        findViewById(R.id.text_criar_conta).setOnClickListener(v ->
                startActivity(new Intent(this, CriarContaActivity.class)));
        //    findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        findViewById(R.id.recuperar_conta).setOnClickListener(v ->
                startActivity(new Intent(this, RecuperarContaActivity.class)));
    }

    public void validaDados(View view) {
        String email = edt_email.getText().toString();
        String senha = edt_senha.getText().toString();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {

                ocultarTeclado();

                progressBar.setVisibility(View.VISIBLE);

                logar(email, senha);

            } else {
                edt_senha.requestFocus();
                edt_senha.setError("Informe sua senha.");
            }
        } else {
            edt_email.requestFocus();
            edt_email.setError("Informe seu email.");
        }
    }

    private void logar(String email, String senha) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                verificaCadastro(task.getResult().getUser().getUid());
            } else {
                progressBar.setVisibility(View.GONE);
                erroAutenticacao(FirebaseHelper.validaErros(task.getException().getMessage()));
            }
        });
    }

    private void verificaCadastro(String idUser) {
        DatabaseReference loginRef = FirebaseHelper.getDatabaseReference()
                .child("login")
                .child(idUser);
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                login = snapshot.getValue(Login.class);
                verificaAcesso(login);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificaAcesso(Login login) {
        if (login != null) {
            if (login.getTipo().equals("U")) {
                if (login.getAcesso()) {
                    setResult(RESULT_OK,new Intent());
                    finish();
                } else {
                    recuperaUsuario();
                }
            } else {
                if (login.getAcesso()) {
                    finish();
                    startActivity(new Intent(getBaseContext(), EmpresaHomeActivity.class));
                } else {
                    recuperaEmpresa();
                }
            }

        }
    }

    private void recuperaUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(login.getId());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null) {
                    finish();
                    Intent intent = new Intent(getBaseContext(), UsuarioFinalizaCadastroActivity.class);
                    intent.putExtra("login", login);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaEmpresa() {
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(login.getId());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Empresa empresa = snapshot.getValue(Empresa.class);
                if (empresa != null) {
                    finish();
                    Intent intent = new Intent(getBaseContext(), EmpresaFinalizaCadastroActivity.class);
                    intent.putExtra("login", login);
                    intent.putExtra("empresa", empresa);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void erroAutenticacao(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void iniciaComponentes() {
//        TextView text_toolbar = findViewById(R.id.text_toolbar);
//        text_toolbar.setText("Login");

        edt_email = findViewById(R.id.edt_email);
        edt_senha = findViewById(R.id.edt_senha);
        progressBar = findViewById(R.id.progressBar);
        facebook = findViewById(R.id.fab_facebook);
        twitter = findViewById(R.id.fab_twitter);
        google = findViewById(R.id.fab_google);


        edt_email = findViewById(R.id.edt_email);
        edt_senha = findViewById(R.id.edt_senha);

        edt_email.setTranslationX(800);
        edt_senha.setTranslationX(800);

        edt_email.setAlpha(v);
        edt_senha.setAlpha(v);


        edt_email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        edt_senha.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();


        facebook.setTranslationY(300);
        google.setTranslationY(300);
        twitter.setTranslationY(300);

        facebook.setAlpha(v);
        google.setAlpha(v);
        twitter.setAlpha(v);

        facebook.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        twitter.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();


    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edt_email.getWindowToken(), 0
        );
    }


}