import axios from "axios"
export const BASEURL=`http://localhost:8080/api/v1/email`;
export const customAxios=axios.create({
    baseURL:BASEURL,
})