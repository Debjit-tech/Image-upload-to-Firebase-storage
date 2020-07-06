public class MainActivity extends AppCompatActivity {


    CircularImageView profileimg;
    EditText name , phone;
    Button create;

    String Name,Phone;

    FirebaseDatabase database;
    DatabaseReference ref;

    FirebaseStorage storage;
    StorageReference storageReference;

    public Uri imageUri;

    private StorageTask storagetask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        profileimg=findViewById(R.id.image);

        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);

        create=findViewById(R.id.create_btn);

        database=FirebaseDatabase.getInstance();

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name=name.getText().toString();
                Phone=phone.getText().toString();

                if(Name.isEmpty() && Phone.isEmpty()){
                    Toast.makeText(MainActivity.this,"First fill your details.",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(storagetask!=null && storagetask.isInProgress()){
                        Toast.makeText(MainActivity.this,"Upload in progress.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        uploadImage();
                    }
                }

            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                uploadDetails();

                final ProgressDialog pdialog= new ProgressDialog(MainActivity.this);
                pdialog.setTitle("Creating Profile.....");
                pdialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("Username",Name);
                        startActivity(intent);
                    }
                },2500);

                for(int i =0 ;i <=2500 ; i=i+1000) {

                    double progresspercent = (100.00 * 2500 / 2500);
                    pdialog.setMessage("Percent: "+(int) progresspercent + "%");
                }

            }
        });

    }


    private void uploadDetails() {

        Name=name.getText().toString();
        Phone=phone.getText().toString();

        if(Name.isEmpty()){
            name.setError("Give a name");
            getCurrentFocus();
        }

        if(Phone.isEmpty()){
            phone.setError("Give a mobile no.");
            getCurrentFocus();
        }

        if(!Name.isEmpty() && !Phone.isEmpty()) {

            ref = database.getReference().child("User");

            HashMap<Object, String> hashMap = new HashMap<>();

            hashMap.put("Username", Name);
            hashMap.put("Userphone", Phone);

            ref.child(Name).setValue(hashMap);
        }
    }


    private void uploadImage() {

        Intent i =new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            imageUri = data.getData();
            profileimg.setImageURI(imageUri);
            uploadtoStorage();
        }
    }

    private void uploadtoStorage() {

        final ProgressDialog pd= new ProgressDialog(this);
        pd.setTitle("Uploading Image.....");
        pd.show();


        StorageReference storageref = storageReference.child(Name+".jpg");

        storagetask = storageref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"Image uploaded.",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        pd.dismiss();
                       Toast.makeText(MainActivity.this,"Image not uploaded.",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progresspercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("Percentage: "+(int) progresspercent+ "%");
                    }
                });
    }
}
