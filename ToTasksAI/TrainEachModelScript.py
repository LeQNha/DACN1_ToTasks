# TRAIN START TIME PREDICTION MODEL
# Chuyển StartTime thành số phút từ đầu ngày
from TrainScript import task_name_vectorized
from prediction_models.train_models.StartTimeTrain2 import train_start_time_prediction_model_2
from utils.DataPreparation import used_data


def time_to_minutes(start_time):
    hours, minutes = map(int, start_time.split(":"))
    return hours * 60 + minutes

used_data['StartTimeMinutes'] = used_data['StartTime'].apply(time_to_minutes)

required_columns = used_data[['Type', 'Importance', 'DayOfWeek']]
train_start_time_prediction_model_2(task_name_vectorized, required_columns=required_columns,
                                  start_time_minutes=used_data['StartTimeMinutes'])