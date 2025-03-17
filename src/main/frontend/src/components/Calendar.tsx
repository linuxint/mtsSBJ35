import { useState } from 'react';
import { 
  Box, 
  Card, 
  CardContent, 
  Typography, 
  Grid, 
  Button, 
  Paper,
  List,
  ListItem,
  ListItemText,
  Divider
} from '@mui/material';
import { CalendarData } from '../services/mainService';
import { fetchCalendarData } from '../services/mainService';

interface CalendarProps {
  initialData: CalendarData;
}

const Calendar = ({ initialData }: CalendarProps) => {
  const [calendarData, setCalendarData] = useState<CalendarData>(initialData);
  const [loading, setLoading] = useState(false);

  const handleWeekChange = async (date: string) => {
    setLoading(true);
    try {
      const data = await fetchCalendarData(date);
      setCalendarData(data);
    } catch (error) {
      console.error('Error fetching calendar data:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderDayEvents = (day: any) => {
    if (!day.list || day.list.length === 0) {
      return <Typography variant="body2" color="text.secondary">일정 없음</Typography>;
    }

    return (
      <List dense disablePadding>
        {day.list.map((event: any, index: number) => (
          <Box key={event.id || index}>
            {index > 0 && <Divider component="li" />}
            <ListItem disablePadding sx={{ py: 0.5 }}>
              <ListItemText
                primary={event.title}
                secondary={event.time}
                primaryTypographyProps={{ variant: 'body2' }}
                secondaryTypographyProps={{ variant: 'caption' }}
              />
            </ListItem>
          </Box>
        ))}
      </List>
    );
  };

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography variant="h6" component="h2">
            캘린더 ({calendarData.month}월)
          </Typography>
          <Box>
            <Button 
              size="small" 
              onClick={() => handleWeekChange(calendarData.preWeek)}
              disabled={loading}
            >
              이전 주
            </Button>
            <Button 
              size="small" 
              onClick={() => handleWeekChange(calendarData.nextWeek)}
              disabled={loading}
            >
              다음 주
            </Button>
          </Box>
        </Box>

        <Grid container spacing={1}>
          {calendarData.calenList && calendarData.calenList.map((day, index) => (
            <Grid item xs={12} sm={6} md={3} lg={12/7} key={day.date || index}>
              <Paper 
                elevation={1} 
                sx={{ 
                  p: 1, 
                  height: '100%',
                  bgcolor: day.istoday ? 'primary.light' : 'background.paper',
                  color: day.istoday ? 'primary.contrastText' : 'text.primary',
                }}
              >
                <Typography variant="subtitle2" gutterBottom>
                  {day.date}
                </Typography>
                {renderDayEvents(day)}
              </Paper>
            </Grid>
          ))}
        </Grid>
      </CardContent>
    </Card>
  );
};

export default Calendar;
