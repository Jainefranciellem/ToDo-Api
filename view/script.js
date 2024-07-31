const url = "http://localhost:8080/task/user/2";

const hideLoader = () => {
    document.getElementById("loading").style.display = "none";
}

const showTasks = (tasks) => {
    // const taskList = document.getElementById("task-list");
    // taskList.innerHTML = "";
    // tasks.forEach(task => {
    //     const taskItem = document.createElement("li");
    //     taskItem.innerHTML = task.name;
    //     taskList.appendChild(taskItem);
    // });

    let tab = `<thead>
                    <th>Task</th>
                    <th>Description</th>
                    <th>Username</th>
                    <th>User Id</th>
                </thead>`;

   for (const task of tasks) {
        tab += `<tr>
                    <td scope="row">${task.name}</td>
                    <td>${task.description}</td>
                    <td>${task.user.username}</td>
                    <td>${task.user.id}</td>
                </tr>`   
   }

   document.getElementById("tasks").innerHTML = tab;
}

const  getAPI = async (url) => {
    const response = await fetch(url, {method: "GET"});
    let data = await response.json();
    if (response) hideLoader();
    console.log(data);
    showTasks(data);
}

getAPI(url);