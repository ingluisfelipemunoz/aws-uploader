import "./App.css";
import axios from "axios";
import { useState, useEffect, useCallback } from "react";
import { useDropzone } from "react-dropzone";
const UserProfiles = () => {
  const [profiles, setProfiles] = useState([]);
  const fetchUserProfiles = () => {
    axios.get("http://localhost:8080/api/v1/user-profile").then((res) => {
      console.log("res", res);
      setProfiles(res?.data || []);
    });
  };

  useEffect(() => {
    fetchUserProfiles();
  }, []);

  function Dropzone({ id }) {
    const onDrop = useCallback(
      (acceptedFiles) => {
        const file = acceptedFiles[0];
        console.log("file", file);
        const data = new FormData();
        data.append("file", file);
        // Do something with the files
        axios
          .post(
            `http://localhost:8080/api/v1/user-profile/${id}/image/upload`,
            data,
            {
              headers: {
                "Content-Type": "multipart/form-data",
              },
            }
          )
          .then((res) => {
            console.log("File uploaded successfully", res);
          })
          .catch((err) => {
            console.log("error-uploading", err);
          });
      },
      [id]
    );
    const { getRootProps, getInputProps, isDragActive } = useDropzone({
      onDrop,
    });

    return (
      <div {...getRootProps()}>
        <input {...getInputProps()} />
        {isDragActive ? (
          <p>Upload Profile Image ...</p>
        ) : (
          <p>Set Profile Image</p>
        )}
      </div>
    );
  }

  return profiles.map((profile, index) => {
    return (
      <div key={index}>
        {profile.id ? (
          <img
            alt="profile-image"
            src={`http://localhost:8080/api/v1/user-profile/${profile.id}/image/download`}
          />
        ) : null}
        <br />
        <br />
        <p className="">{profile.userProfileImageLink}</p>
        <h1>{profile.username}</h1>
        <p>{profile.id}</p>
        <Dropzone id={profile.id} />
        <br />
      </div>
    );
  });
};

function App() {
  return (
    <div className="App">
      <UserProfiles />
    </div>
  );
}

export default App;
