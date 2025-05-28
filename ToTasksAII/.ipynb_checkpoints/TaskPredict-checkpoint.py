from prediction_models.predict_models.DurationPredict import predict_duration
from prediction_models.predict_models.ImportancePredict import predict_importance
from prediction_models.predict_models.StartTimePredict2 import predict_start_time_2
from prediction_models.predict_models.TypePredict import predict_task_type
from utils.DataInput import new_task
from utils.SpellCheck import preprocess_text_with_spell_check
from utils.TextPreprocess import preprocess_text


def task_predict(new_task):
    print("DEBUG new_task:", new_task)
    
    original_name = new_task['TaskName']

    # Xử lí dữ liệu
    # # Làm sạch và tiền xử lý TaskName input
    # new_task['TaskName'] = preprocess_text(original_name)
    # Tiền xử lý thêm: Loại bỏ stopwords, lemmatization và sửa lỗi chính tả
    new_task['TaskName'] = preprocess_text_with_spell_check(new_task['TaskName'])
    corrected_name = new_task['TaskName']
    # In ra sự khác biệt giữa tên gốc và tên đã sửa lỗi chính tả
    print(f"Original Task Name: {original_name} -> Corrected Task Name: {corrected_name}")

    # PREDICT TYPE
    if new_task['Type'] == "":  # hoặc if not var: để kiểm tra chuỗi rỗng
        # Biến này rỗng
        # task_type_predicted = predict_task_type(new_task['TaskName'], new_task['UserId'])
        task_type_predicted = predict_task_type(new_task['TaskName'])
        new_task['Type'] = task_type_predicted
        print(f"\n Type: {new_task['Type']}")


    # PREDICT IMPORTANCE
    if new_task['Importance'] == "":
        # task_importance_predicted = predict_importance(new_task['TaskName'], new_task['Type'], new_task['UserId'])
        task_importance_predicted = predict_importance(new_task['TaskName'], new_task['Type'])
        new_task['Importance'] = task_importance_predicted
        print(f"Importance: {new_task['Importance']}")


    # PREDICT DURATION
    if new_task['Duration'] == 0:
        # task_duration_predict = predict_duration(task_name=new_task['TaskName'], task_type=new_task['Type'],
        #                                          task_importance=new_task['Importance'], day_of_week=new_task['DayOfWeek'], user_id=new_task['UserId'])
        task_duration_predict = predict_duration(task_name=new_task['TaskName'], task_type=new_task['Type'],
                                                 task_importance=new_task['Importance'], day_of_week=new_task['DayOfWeek'])

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
        # predicted_start_time = predict_start_time_2(task_name=new_task['TaskName'], task_type=new_task['Type'],
        #                                             importance=new_task['Importance'], day_of_week=new_task['DayOfWeek'], user_id=new_task['UserId'])
        predicted_start_time = predict_start_time_2(task_name=new_task['TaskName'], task_type=new_task['Type'],
                                                    importance=new_task['Importance'], day_of_week=new_task['DayOfWeek'])
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
                        # task['Duration'] -= overlap_duration
                        # task['EndTimeInMinute'] = task['StartTimeInMinute'] + task['Duration']
                        # task['EndTime'] = format_time(task['EndTimeInMinute'])

                        task['Duration'] -= overlap_duration
                        task['StartTimeInMinute'] = placed['EndTimeInMinute']
                        task['EndTimeInMinute'] = task['StartTimeInMinute'] + task['Duration']
                        task['StartTime'] = format_time(task['StartTimeInMinute'])
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

# def move_to_free_slot(task, placed_tasks):
#     # tìm khoảng trống từ 8:00 đến 20:00
#     work_start = 7 * 60
#     work_end = 21 * 60

#     occupied = sorted(placed_tasks, key=lambda x: x['StartTimeInMinute'])

#     candidate_start = work_start

#     for t in occupied:
#         if candidate_start + task['Duration'] <= t['StartTimeInMinute']:
#             # Có khoảng trống đủ
#             return set_task_time(task, candidate_start)
#         else:
#             candidate_start = max(candidate_start, t['EndTimeInMinute'])

#     # Nếu không có chỗ → cho vào cuối cùng nếu còn thời gian
#     if candidate_start + task['Duration'] <= work_end:
#         return set_task_time(task, candidate_start)
#     else:
#         # Không còn chỗ → giữ nguyên (hoặc có thể đánh dấu là không thể xếp)
#         return task

# # move_to_free_slot 1 
# def move_to_free_slot(task, placed_tasks):
#     buffer = 240  # phút – khoảng thời gian cho phép tìm quanh thời gian mong muốn
#     preferred_start = task['StartTimeInMinute']
    
#     search_start = max(0, preferred_start - buffer)
#     search_end = preferred_start + buffer

#     occupied = sorted(placed_tasks, key=lambda x: x['StartTimeInMinute'])

#     candidate_start = search_start

#     for t in occupied:
#         if candidate_start + task['Duration'] <= t['StartTimeInMinute']:
#             # Có khoảng trống đủ
#             return set_task_time(task, candidate_start)
#         else:
#             candidate_start = max(candidate_start, t['EndTimeInMinute'])
#             if candidate_start > search_end:
#                 break  # Vượt quá vùng tìm kiếm cho phép

#     # Nếu không có chỗ → giữ nguyên
#     return task

# move_to_free_slot 2 
# def move_to_free_slot(task, placed_tasks):
#     preferred_start = task['StartTimeInMinute']
#     duration = task['Duration']
    
#     # Danh sách occupied đã sắp xếp
#     occupied = sorted(placed_tasks, key=lambda x: x['StartTimeInMinute'])

#     # Danh sách các khoảng trống [(start, end)]
#     free_slots = []

#     # Bắt đầu từ 0 đến thời gian của task đầu tiên
#     if occupied:
#         if occupied[0]['StartTimeInMinute'] > 0:
#             free_slots.append((0, occupied[0]['StartTimeInMinute']))

#         for i in range(len(occupied) - 1):
#             end_current = occupied[i]['EndTimeInMinute']
#             start_next = occupied[i + 1]['StartTimeInMinute']
#             if start_next > end_current:
#                 free_slots.append((end_current, start_next))

#         # Khoảng trống sau task cuối cùng
#         free_slots.append((occupied[-1]['EndTimeInMinute'], 24 * 60))  # Giới hạn 24h
#     else:
#         free_slots.append((0, 24 * 60))

#     # Tìm slot gần thời gian mong muốn nhất
#     best_slot = None
#     min_distance = float('inf')

#     for start, end in free_slots:
#         if end - start >= duration:
#             # Có chỗ trống đủ
#             dist = abs(start - preferred_start)
#             if dist < min_distance:
#                 min_distance = dist
#                 best_slot = start

#     # Nếu tìm được slot phù hợp
#     if best_slot is not None:
#         return set_task_time(task, best_slot)

#     # Nếu không tìm được slot nào (hiếm gặp)
#     return task

def move_to_free_slot(task, placed_tasks):
    preferred_start = task['StartTimeInMinute']
    duration = task['Duration']
    
    work_start = 6 * 60  # 07:00
    work_end = 22 * 60   # 21:00

    occupied = sorted(placed_tasks, key=lambda x: x['StartTimeInMinute'])
    free_slots = []

    if occupied:
        if occupied[0]['StartTimeInMinute'] > work_start:
            free_slots.append((work_start, occupied[0]['StartTimeInMinute']))

        for i in range(len(occupied) - 1):
            end_current = occupied[i]['EndTimeInMinute']
            start_next = occupied[i + 1]['StartTimeInMinute']
            if start_next > end_current:
                free_slots.append((end_current, start_next))

        if occupied[-1]['EndTimeInMinute'] < work_end:
            free_slots.append((occupied[-1]['EndTimeInMinute'], work_end))
    else:
        free_slots.append((work_start, work_end))

    best_slot = None
    min_distance = float('inf')

    for start, end in free_slots:
        if end - start >= duration:
            dist = abs(start - preferred_start)
            if dist < min_distance:
                min_distance = dist
                best_slot = start

    if best_slot is not None:
        return set_task_time(task, best_slot)

    # Không tìm được slot phù hợp → giữ nguyên
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
