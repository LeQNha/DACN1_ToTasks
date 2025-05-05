from prediction_models.predict_models.DurationPredict import predict_duration
from prediction_models.predict_models.ImportancePredict import predict_importance
from prediction_models.predict_models.StartTimePredict import predict_start_time
from prediction_models.predict_models.StartTimePredict2 import predict_start_time_2
from prediction_models.predict_models.TypePredict import predict_task_type
from utils.DataInput import new_task
from utils.SpellCheck import preprocess_text_with_spell_check
from utils.TextPreprocess import preprocess_text
from datetime import datetime, timedelta


def task_predict(new_task):
    original_name = new_task['TaskName']

    # Xử lí dữ liệu
    # Làm sạch và tiền xử lý TaskName input
    new_task['TaskName'] = preprocess_text(original_name)
    # Tiền xử lý thêm: Loại bỏ stopwords, lemmatization và sửa lỗi chính tả
    new_task['TaskName'] = preprocess_text_with_spell_check(new_task['TaskName'])
    corrected_name = new_task['TaskName']
    # In ra sự khác biệt giữa tên gốc và tên đã sửa lỗi chính tả
    print(f"Original Task Name: {original_name} -> Corrected Task Name: {corrected_name}")

    # PREDICT TYPE
    if new_task['Type'] == "":  # hoặc if not var: để kiểm tra chuỗi rỗng
        # Biến này rỗng
        task_type_predicted = predict_task_type(new_task['TaskName'])
        new_task['Type'] = task_type_predicted
        print(f"\n Type: {new_task['Type']}")

    # PREDICT IMPORTANCE
    if new_task['Importance'] == "":
        task_importance_predicted = predict_importance(new_task['TaskName'], new_task['Type'])
        new_task['Importance'] = task_importance_predicted
        print(f"Importance: {new_task['Importance']}")

    # PREDICT DURATION
    if new_task['Duration'] == 0:
        task_duration_predict = predict_duration(task_name=new_task['TaskName'], task_type=new_task['Type'],
                                                 task_importance=new_task['Importance'],
                                                 day_of_week=new_task['DayOfWeek'])

        print(f"Predicted Duration: {task_duration_predict:.2f} minutes")
        predicted_duration = round(task_duration_predict / 5) * 5
        new_task['Duration'] = predicted_duration
        print(f"Final duration predicted: {new_task['Duration']}")

    # # PREDICT START TIME
    # predicted_start_time = predict_start_time(task_name=new_task['TaskName'], task_type=new_task['Type'],
    #                                           importance=new_task['Importance'], day_of_week=new_task['DayOfWeek'])
    # print(f"Predicted Start Time (in minutes): {predicted_start_time}")
    #
    #
    def minutes_to_hours_minutes(minutes):
        hours = int(minutes // 60)  # Chia lấy số giờ
        minutes = int(minutes % 60)  # Lấy phần dư để tính phút
        return f"{hours:02}:{minutes:02}"

    #
    #
    # # Chuyển đổi phút thành giờ:phút
    # predicted_start_time_formatted = minutes_to_hours_minutes(predicted_start_time)
    # new_task['StartTime'] = predicted_start_time_formatted
    # print(f"Predicted Start Time: {new_task['StartTime']}")
    # print(f"\n {new_task}")

    # PREDICT START TIME 2
    predicted_start_time = new_task['StartTimeInMinute']
    if new_task['StartTime'] == "":
        print("\n START TIME PREDICT 2")
        predicted_start_time = predict_start_time_2(task_name=new_task['TaskName'], task_type=new_task['Type'],
                                                    importance=new_task['Importance'],
                                                    day_of_week=new_task['DayOfWeek'])
        predicted_start_time = round(predicted_start_time / 5) * 5
        new_task['StartTimeInMinute'] = predicted_start_time
        print(f"Predicted Start Time (in minutes): {predicted_start_time}")

        # Chuyển đổi phút thành giờ:phút
        predicted_start_time_formatted = minutes_to_hours_minutes(predicted_start_time)
        new_task['StartTime'] = predicted_start_time_formatted
        print(f"Predicted Start Time: {new_task['StartTime']}")
        print(f"Predicted End Time: {new_task['EndTime']}")

    # Tính toán EndTime
    predicted_end_time = predicted_start_time + new_task['Duration']
    predicted_end_time = round(predicted_end_time / 5) * 5
    new_task['EndTimeInMinute'] = predicted_end_time
    print(f"Predicted End Time (in minutes): {predicted_end_time}")
    # Chuyển đổi phút thành giờ:phút
    predicted_end_time_formatted = minutes_to_hours_minutes(predicted_end_time)
    new_task['EndTime'] = predicted_end_time_formatted

    print(f"\n {new_task}")
    return new_task


# OPTIMIZATION
importance_map = {'Very Important': 3, 'Important': 2, 'Normal': 1, 'Less Important': 0}


def is_overlap(a, b):
    return not (a['EndTimeInMinute'] <= b['StartTimeInMinute'] or a['StartTimeInMinute'] >= b['EndTimeInMinute'])

def task_optimize(task_list):
    # Bước 1: Sắp xếp theo độ ưu tiên giảm dần, sau đó theo thời gian bắt đầu
    task_list.sort(key=lambda x: (-importance_map.get(x['Importance'], 0), x['StartTimeInMinute']))

    placed_tasks = []

    for task in task_list:
        has_conflict = False

        for placed in placed_tasks:
            if is_overlap(task, placed):
                has_conflict = True
                overlap_start = max(task['StartTimeInMinute'], placed['StartTimeInMinute'])
                overlap_end = min(task['EndTimeInMinute'], placed['EndTimeInMinute'])
                overlap_duration = overlap_end - overlap_start

                task_priority = importance_map.get(task['Importance'], 0)
                placed_priority = importance_map.get(placed['Importance'], 0)

                if task_priority < placed_priority:
                    # Bị trùng - task này thấp hơn, xử lý
                    if overlap_duration <= task['Duration'] / 4:
                        # Chỉ cắt bớt phần bị trùng
                        task['Duration'] -= overlap_duration
                        task['EndTimeInMinute'] = task['StartTimeInMinute'] + task['Duration']
                        task['EndTime'] = format_time(task['EndTimeInMinute'])
                    else:
                        # Dời task
                        task = move_to_free_slot(task, placed_tasks)
                elif task_priority == placed_priority:
                    # Nếu cùng độ ưu tiên, dời task hiện tại
                    task = move_to_free_slot(task, placed_tasks)
                else:
                    # Task này cao hơn → giữ nguyên
                    pass

                break  # xử lý xong xung đột với 1 task là đủ

        if not has_conflict:
            # Không có xung đột, giữ nguyên
            pass

        placed_tasks.append(task)

    return placed_tasks

def move_to_free_slot(task, placed_tasks):
    # Giả lập: tìm khoảng trống từ 8:00 đến 20:00
    work_start = 1 * 60
    work_end = 23 * 60

    occupied = sorted(placed_tasks, key=lambda x: x['StartTimeInMinute'])

    candidate_start = work_start

    for t in occupied:
        if candidate_start + task['Duration'] <= t['StartTimeInMinute']:
            # Có khoảng trống đủ
            return set_task_time(task, candidate_start)
        else:
            candidate_start = max(candidate_start, t['EndTimeInMinute'])

    # Nếu không có chỗ → cho vào cuối cùng nếu còn thời gian
    if candidate_start + task['Duration'] <= work_end:
        return set_task_time(task, candidate_start)
    else:
        # Không còn chỗ → giữ nguyên (hoặc có thể đánh dấu là không thể xếp)
        return task

def set_task_time(task, start_minute):
    task['StartTimeInMinute'] = start_minute
    task['StartTime'] = format_time(start_minute)
    task['EndTimeInMinute'] = start_minute + task['Duration']
    task['EndTime'] = format_time(task['EndTimeInMinute'])
    return task

def format_time(minute):
    hour = minute // 60
    min_part = minute % 60
    return f"{hour:02d}:{min_part:02d}"