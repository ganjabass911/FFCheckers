package checkers;
     
import java.util.Vector;
//реализация игрового дерева
public class GameEngine {

    final static int inf = Integer.MAX_VALUE;
    final static int normal = 100;         //стоимость простой шашки
    final static int king=200;             //стоимость дамки
    final static int pos=1;                //одна позиция по -j стоит 1
    final static int edge=10;               // эффект короля на краю

/*********************  Функция оценки  **********************************

Для обычной шашки;
    Оценка=(ВесШашки + ВесПозиции*(позицию)^2)*НомерШашки + РандомнаяВеличина;

Для дамки;
    Оценка=(ВесДамки - ВесКрая*НомерКрая)*НомерДамки + РандомнаяВеличина;

Вес шашки
     Собственная   = 100
     Противоположная  = -100
Для дамки
     Собственная = 200
     Противоположная = -200
  
    Для нормальной шашки это вес позиции.
    Оценки меньше на краях для королей, так что они, как правило, находятся в середине
    Малая рандломная велицина между 0-10 будет добавлена для уменьшения монотонности игры.
**********************************************************************************/

    public static int eval(int [][] board){
        int score=0;

        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
	            if (board[i][j] == Checkers.redNormal)
                {
                      score-=normal;
                      score-=pos*j*j;
                }

                else if (board[i][j] ==Checkers.redKing){
                    score-=king;
                    if (i==0 || i==7)
                        score += edge;
                    if (j==0 || j==7)
                        score += edge;
                }

                else if (board[i][j] == Checkers.yellowNormal)
                {
                      score+=normal;
                      score+=pos*(7-j)*(7-j);
                }

                else if (board[i][j] == Checkers.yellowKing){
                    score+=king;
                    if (i==0 || i==7)
                        score -= edge;
                    if (j==0 || j==7)
                        score -= edge;
                }
            }
        }
        score += (int)(Math.random() * 10);                    //добавление рандомной величины

        return score;
    }

/********************  MinMax с Alpha-Beta отсечением  ************************
 * Возврашаем наилучшую оценку в MinMax  с alpha beta отсечением
 * отрезаем дерево в соответствии с уровнем сложности
 * С максимальной степенью сложности он ищет до 6 уровней для наилучшего возможного решения
 * С минимальной сложностью он ищет только два уровня.
****************************************************************************/

      public static int MinMax(int[][] board, int depth, int maxDepth, int[] move, int toMove, int[] counter)
      {
          return MinMax(board,depth,maxDepth,move,toMove,counter,inf,-inf);
      }

      static int MinMax(int[][] board, int depth, int maxDepth,int[] move, int turn, int[] counter, int redBest, int yellowBest)
      {
        int score,bestScore;
        int[][] newBoard;
        int[] bestMove=new int[4];

        Vector movesList;  //вектор 4x1 массивов

        //предполагается, что глубина никогда не равна maxDepth, так как selectedMove здесь не задан

        if (depth==maxDepth)
        {
            bestScore = eval(board);
            counter[0]++;
        }

        else {
            movesList = CheckerMove.generateMoves(board,turn);
            bestScore=getTurn(turn);
            switch (movesList.size())
            {
            case 0:
                counter[0]++;
                return bestScore;
            case 1:
              if (depth == 0)
              {
                  // принудительный ход: немедленно вернуть управление
                bestMove = (int[])movesList.elementAt(0);
                  System.arraycopy(bestMove, 0, move, 0, 4);
                return 0;
              }
              else
              {
                  // расширяем поиск, поскольку есть принудительный ход
                  maxDepth += 1;
              }
            }

            for (int i=0;i<movesList.size();i++){
                newBoard = copyBoard(board);
                CheckerMove.moveComputer(newBoard, (int[])movesList.elementAt(i)); /*возвращает newBoard (изменяя начальную
                                                                                и конечную координаты и применяя переход)*/
                int temp[] = new int[4];
                score= MinMax(newBoard, depth+1, maxDepth, temp, getOpponent(turn), counter, yellowBest, redBest);

                if (turn==Checkers.yellowNormal && score > bestScore) {
                    bestMove = (int[])movesList.elementAt(i);
                    bestScore = score;
                    if (bestScore > yellowBest)
                    {
                        if (bestScore >= redBest)
                            break;  /*  alpha_beta сечение  */
                        else
                            yellowBest = bestScore;  //Оценка
                    }
                }

                else if (turn==Checkers.redNormal && score < bestScore) {
                    bestMove = (int[])movesList.elementAt(i);
                    bestScore = score;
                    if (bestScore < redBest)
                    {
                        if (bestScore <= yellowBest)
                            break;  /*  alpha_beta сечение  */
                        else
                            redBest = bestScore;  //оценка
                    }
                }
            }
        }

          System.arraycopy(bestMove, 0, move, 0, 4);
        return bestScore;
    }

      static int[][] copyBoard(int[][] board){       //Используем копирование для матрицы
        int[][] copy = new int[8][8];

        for (int i=0; i<8; i++)
          System.arraycopy(board[i],0,copy[i],0,8);
        return copy;
      }
    
    static int getOpponent(int turn){                  //Возвращаемся к противнику
         return turn==Checkers.yellowNormal ? Checkers.redNormal : Checkers.yellowNormal;
    }

    static int getTurn(int turn) {                     //returns the turn
          return CheckerMove.colour(turn)==Checkers.yellowNormal ? -inf : inf;
    }
}