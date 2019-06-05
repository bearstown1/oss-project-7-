package com.example.offender_location5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
/*이친구는 일단 오류 수정 사항..?*/
import java.text.BreakIterator;


public class SignUpActivity extends AppCompatActivity {
    EditText editText3; //이름
    EditText editText4; //폰 번호
    RadioButton radioButton4; //여성
    RadioButton radioButton3; //남성
    EditText editText5; //생일
    RadioButton radioButton6; //부모
    RadioButton radioButton5; //자녀
    Button button3; //회원가입 저장
    FirebaseAuth firebaseAuth;
    private BreakIterator textviewmassage; //이것도 오류수정..?

    //버튼 액션
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reactivity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        /*형변환후 저장*/
        editText2 = (EditText) findViewById(R.id.editText2);//이메일
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);//패스워드
        button3 = (Button) findViewById(R.id.button3);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
        radioButton5 = (RadioButton) findViewById(R.id.radioButton5);
        radioButton6 = (RadioButton) findViewById(R.id.radioButton6);
        final ProgressDialog progressDialog = new ProgressDialog(this);


        private void registerUser() { //컴파일러가 함수를 이해하지 못한다는 이야기임
            String phonenumber = editText4.getText().toString().trim(); //핸드폰 번호 가져옴
            String email = editText2.getText().toString().trim();
            String password = editText6.getText().toString().trim();
            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(password)){
                Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(phonenumber)) {
                Toast.makeText(this, "핸드폰 번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.setMessage("회원가입중 입니다....");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        //에러발생시
                        textviewmassage.setText("에러유형\n - 이미 등록된 이메일  \n -암호 최소 6자리 이상 \n - 서버에러");
                        Toast.makeText(SignUpActivity.this, "등록 에러!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivityForResult(intent, 101);
                }
            });
        }
    }

}
