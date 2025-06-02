from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional, Dict, Tuple

from TaskPredict import task_predict
from TaskPredict import task_optimize

from utils.SpellCheck import correct_spelling

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
    UserId: str


@app.post("/predict_task")
async def predict_task(task: Task):
    # # Lấy dữ liệu từ request
    # task_name = task.TaskName
    # task_type = task.Type
    # importance = task.Importance
    # day_of_week = task.DayOfWeek
    # duration = task.Duration
    # start_time = task.StartTime

    originTaskName = correct_spelling(task.TaskName)

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
        "UserId": task.UserId
    }

    task_predicted = task_predict(new_task)

    new_task = {
            "TaskId": task_predicted['TaskId'],
            # "TaskName": task_predicted['TaskName'],
            "TaskName": originTaskName,
            "Type": task_predicted['Type'],
            "Importance": task_predicted['Importance'],
            "DayOfWeek": task_predicted['DayOfWeek'],
            "Duration": task_predicted['Duration'],
            "StartTimeInMinute": task_predicted['StartTimeInMinute'],
            "StartTime": task_predicted['StartTime'],
            "EndTimeInMinute": task_predicted['EndTimeInMinute'],
            "EndTime": task_predicted['EndTime'],
            "UserId": task_predicted['UserId']
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
    for i, task in enumerate(optimized):
        print(f"Task {i+1}: {task}")
    
    return optimized
