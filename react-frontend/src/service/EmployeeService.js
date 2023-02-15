import axios from 'axios'

const EMPLOYEE_SERVICE_BASE_URL = "http://localhost:9191/api/employees";

const EMPLOYEE_ID = 4;

class EmployeeService {

    static getEmployee() {
        return axios.get(EMPLOYEE_SERVICE_BASE_URL + '/' + EMPLOYEE_ID);
    }
}

export default EmployeeService;