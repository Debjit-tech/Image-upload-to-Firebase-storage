public class Main2Activity extends AppCompatActivity {


    CircularImageView profileimg;

    TextView uname,uphone;

    String UNAME;

    FirebaseDatabase database;
    DatabaseReference myref;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        profileimg=findViewById(R.id.image2);

        uname=findViewById(R.id.uname);
        uphone=findViewById(R.id.uphone);

        database= FirebaseDatabase.getInstance();

        UNAME=getIntent().getStringExtra("Username");

        myref=database.getReference().child("User").child(UNAME);

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReferenceFromUrl("gs://database-app-5203d.appspot.com").child(UNAME+".jpg");

        final ProgressBar pgbar=findViewById(R.id.pgbar);
        final TextView details=findViewById(R.id.details);


        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {


                String u_name=ds.child("Username").getValue().toString();
                String u_phone = ds.child("Userphone").getValue().toString();

                uname.setText("Name : " + u_name);
                uphone.setText("Phone : " + u_phone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            final File file =File.createTempFile("image","jpg");

            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                    profileimg.setImageBitmap(bitmap);
                    pgbar.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Main2Activity.this,"Failed to load image.",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
