import React, { useEffect, useRef, useState } from "react";
import toast from "react-hot-toast";
import { getAllEmail, sendEmail, sendEmailWithFile,getAIProcessEmail } from "../services/email.service";
import { Editor } from "@tinymce/tinymce-react";

function EmailSender() {
  const editorRef = useRef(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [messages, setMessages] = useState([]);
  const [emailData, setEmailData] = useState({
    to: "",
    subject: "",
    body: "",
  });
  const [AIChackerData, setAIChackerData] = useState({
    subject: "",
    body: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [isProcess, setIsProcess] = useState(false);
  const [isFetchingEmails, setIsFetchingEmails] = useState(true);

  // Fetch emails on component load
  useEffect(() => {
    setIsFetchingEmails(true);
    getAllEmail()
      .then((response) => {
        console.log("Emails fetched successfully:", response);
        setMessages(response.data);
      })
      .catch((error) => {
        console.error("Error fetching emails:", error);
        toast.error("Failed to fetch emails. Please try again later.");
      })
      .finally(() => {
        setIsFetchingEmails(false);
      });
  }, []);

  // Handle form field changes
  const handleFieldChange = (event, fieldName) => {
    setEmailData({
      ...emailData,
      [fieldName]: event.target.value,
    });
  };

  // Clear form data
  const clearData = () => {
    setEmailData({
      to: "",
      subject: "",
      body: "",
    });
    editorRef.current.setContent("");
    setSelectedFile(null);
  };

  // Submit form data
  const handleSubmit = (event) => {
    event.preventDefault();

    if (!emailData.to || !emailData.subject || !emailData.body) {
      toast.error("All fields are required!");
      return;
    }

    setIsLoading(true);
    const sendEmailAction = selectedFile
      ? sendEmailWithFile(emailData, selectedFile)
      : sendEmail(emailData);

    sendEmailAction
      .then(() => {
        toast.success("Email sent successfully!");
        clearData();
      })
      .catch((error) => {
        console.error("Error sending email:", error);
        toast.error("Failed to send email. Please try again.");
      })
      .finally(() => {
        setIsLoading(false);
      });
  };
  const handleAI = (event) => {
    event.preventDefault();
    setIsProcess(true);
    setAIChackerData({
      subject: emailData.subject,
      body: emailData.body,
    });
    getAIProcessEmail(AIChackerData)
      .then((response) => {
        // console.log("AI processed successfully:", response);
        toast.success("AI processed successfully!");
        setEmailData({
          ...emailData,
          subject: response.data.subject,
          body: response.data.body,
        });
        editorRef.current.setContent(response.data.body);
      })
      .catch((error) => {
        // console.error("Error processing AI:", error);
        toast.error("Failed to process AI. Please try again later.");
      })
      .finally(() => {
        setIsProcess(false);
      });

  }
  return (
    <div className="w-full h-full flex flex-col space-y-5 justify-center items-center min-h-screen">
      {/* Email Sender Form */}
      <div className="email_card w-auto  shadow p-4 rounded space-y-4 bg-slate-300 hover:bg-slate-200 border-[2px] border-blue-500">
        <h1 className="text-gray-900 text-3xl">Email Sender</h1>
        <p className="text-gray-500">Send an email to your favorite person with your own app!</p>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* To Email */}
          <div className="input_field">
            <label htmlFor="email" className="block mb-2 text-sm font-medium text-gray-900">
              To Email
            </label>
            <input
              aria-label="Recipient email"
              value={emailData.to}
              onChange={(event) => handleFieldChange(event, "to")}
              type="email"
              id="email"
              className="bg-gray-50 border border-gray-300 text-sm rounded-lg w-full p-2.5"
              placeholder="Enter recipient email"
              required
            />
          </div>
          {/* Subject */}
          <div className="input_field">
            <label htmlFor="subject" className="block mb-2 text-sm font-medium text-gray-900">
              Subject
            </label>
            <input
              aria-label="Email subject"
              value={emailData.subject}
              onChange={(event) => handleFieldChange(event, "subject")}
              type="text"
              id="subject"
              className="bg-gray-50 border border-gray-300 text-sm rounded-lg w-full p-2.5"
              placeholder="Enter email subject"
              required
            />
          </div>
          {/* Message Body */}
          <div className="input_field">
            <label htmlFor="message" className="block mb-2 text-sm font-medium text-gray-900">
              Message
            </label>
            <Editor
              onEditorChange={(content) => {
                setEmailData((prev) => ({ ...prev, body: content }));
              }}
              apiKey="6e3880od0u1zq1kwutcba35s1pkg8mafkc6i4vrevpw7wf82"
              onInit={(_, editor) => (editorRef.current = editor)}
              initialValue="Welcome to Email Sender!"
              init={{
                plugins:
                  "anchor autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount",
                toolbar:
                  "undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table | align lineheight | numlist bullist indent outdent | emoticons charmap | removeformat",
              }}
            />
          </div>
          {/* File Upload */}
          <div className="input_field p-4">
            <input
              onChange={(event) => setSelectedFile(event.target.files[0])}
              className="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer bg-gray-50"
              type="file"
              id="file_input"
              aria-label="Attach a file"
            />
          </div>
          {/* Buttons */}
          <div className="button_container flex space-x-4 items-center justify-center">
          
            <button
              disabled={isLoading}
              type="submit"
              className={`text-white font-medium rounded-lg text-sm px-5 py-2.5 ${
                isLoading
                  ? "bg-gray-500 cursor-not-allowed"
                  : "bg-blue-700 hover:bg-blue-800"
              }`}
            >
              {isLoading ? "Sending..." : "Send"}
            </button>
            <button
              disabled={isProcess}
              type="button"
              onClick={handleAI}
              className={`text-white font-medium  text-sm px-5 py-2.5 rounded-full ${
                isProcess
                  ? "bg-gray-500 cursor-not-allowed"
                  : "bg-sky-500 hover:bg-sky-800"
              }`}
            >
              <div className="flex items-center space-x-4">
              <div>
              <svg xmlns="http://www.w3.org/2000/svg" width="30px" height="30px" viewBox="0 0 400 400" fill="none">
              <path d="M165.865 345C67.4268 338.855 27.5031 77.085 213.503 69.1662C405.382 60.997 340.806 357.21 197.786 260.179C147.022 225.723 192.405 137.4 241.736 158.785" stroke="#000000" stroke-opacity="0.9" stroke-width="16" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              </div>
              {isProcess ? "AI Proxessing... " : "AI Process"}
              </div>
            </button>
            <button
              type="button"
              onClick={clearData}
              className="text-white bg-red-700 hover:bg-red-800 font-medium rounded-lg text-sm px-5 py-2.5"
            >
              Clear
            </button>
          </div>
        </form>
      </div>

      {/* Email List */}
      {isFetchingEmails ? (
        <div>Loading emails...</div>
      ) : messages.length > 0 ? (
        <div className="w-auto  shadow p-4 rounded bg-slate-300 hover:bg-slate-200 border-[2px] border-blue-500">
          {messages.map((message, index) => (
            <>
            <div key={index} className="email-item">
              <h4 className="font-bold">{message.subject}</h4>
              <p>{message.body}</p>
            </div>
            <hr/></>
          ))}
        </div>
      ) : (
        <p>No emails to display.</p>
      )}
    </div>
  );
}

export default EmailSender;
