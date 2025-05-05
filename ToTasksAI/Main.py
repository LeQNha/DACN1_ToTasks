from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

from TaskPredict import task_predict
from TaskPredict import task_optimize

# Tạo một instance của FastAPI
app = FastAPI()

# Model cho dữ liệu
class Task(BaseModel):
    TaskId: str
    TaskName: str
    Type: str
    Importance: str
    DayOfWeek: str
    Duration: int
    StartTimeInMinute: int
    StartTime: str
    EndTimeInMinute: int
    EndTime: str


@app.post("/predict_task")
async def predict_task(task: Task):
    # # Lấy dữ liệu từ request
    # task_name = task.TaskName
    # task_type = task.Type
    # importance = task.Importance
    # day_of_week = task.DayOfWeek
    # duration = task.Duration
    # start_time = task.StartTime

    new_task = {
        "TaskId": task.TaskId,
        "TaskName": task.TaskName,
        "Type": task.Type,
        "Importance": task.Importance,
        "DayOfWeek": task.DayOfWeek,
        "Duration": task.Duration,
        "StartTimeInMinute": task.StartTimeInMinute,
        "StartTime": task.StartTime,
        "EndTimeInMinute": task.EndTimeInMinute,
        "EndTime": task.EndTime,

    }

    # new_task = [
    #     {
    #         "TaskName": task_name,
    #         "Type": task_type,
    #         "Importance": importance,
    #         "DayOfWeek": day_of_week,
    #         "Duration": duration,  # Thời gian giả lập
    #         "StartTime": start_time  # Thời gian giả lập
    #     },
    #     {
    #         "TaskName": task_name,
    #         "Type": "Personal",
    #         "Importance": "Normal",
    #         "DayOfWeek": "Monday",
    #         "Duration": 45,  # Thời gian giả lập
    #         "StartTime": "09:00 AM"  # Thời gian giả lập
    #     },
    # ]
    #
    # return new_task

    task_predicted = task_predict(new_task)

    # new_task = [
    #     {
    #         "TaskName": task_predicted['TaskName'],
    #         "Type": task_predicted['Type'],
    #         "Importance": task_predicted['Importance'],
    #         "DayOfWeek": task_predicted['DayOfWeek'],
    #         "Duration": task_predicted['Duration'],
    #         "StartTime": task_predicted['StartTime'],
    #         "EndTime": task_predicted['EndTime']
    #     }
    # ]

    new_task = {
            "TaskId": task_predicted['TaskId'],
            "TaskName": task_predicted['TaskName'],
            "Type": task_predicted['Type'],
            "Importance": task_predicted['Importance'],
            "DayOfWeek": task_predicted['DayOfWeek'],
            "Duration": task_predicted['Duration'],
            "StartTimeInMinute": task_predicted['StartTimeInMinute'],
            "StartTime": task_predicted['StartTime'],
            "EndTimeInMinute": task_predicted['EndTimeInMinute'],
            "EndTime": task_predicted['EndTime']
        }

    print(f"___{new_task}")

    return new_task

@app.post("/optimize_schedule")
async def optimize_schedule(tasks: List[Task]):
    task_list = [task.dict() for task in tasks]

    print("=== Danh sách task đầu vào ===")
    for i, task in enumerate(task_list):
        print(f"Task {i + 1}: {task}")

    optimized = task_optimize(task_list)

    # In ra danh sách sau tối ưu
    print("\n=== Danh sách task sau tối ưu ===")
    for i, task in enumerate(optimised_tasks):
        print(f"Task {i + 1}: {task}")
    return optimized