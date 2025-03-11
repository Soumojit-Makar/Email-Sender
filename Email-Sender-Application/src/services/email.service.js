import { data } from "autoprefixer";
import { customAxios } from "./helper";

export async function sendEmail(emailData){
  return await   customAxios.post(`/send`,emailData)
}
export async function sendEmailWithFile(emailData,file) {
  const data=new FormData();
  data.append("file",file);
  data.append("request",JSON.stringify(emailData)) 
  return await  customAxios.post(`/send-with-file`,data)
}
export async function getAllEmail(){
  return  await customAxios.get(`/get`)
}
export async function getAIProcessEmail(data){
  return  await customAxios.post(`/process`,data)
}